package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public final class Main {
	
	public static Logger logger;
	
	public static void main( final String[] args ) throws FileNotFoundException {
		final long begin = System.currentTimeMillis();
		final String inputFilePath;
		final String outputFilePath;
		// final String stopWordsPath = "stopwords_en.txt";
		
		if( args.length >= 2 ) {
			inputFilePath = args[0];
			outputFilePath = args[1];
		} else {
			inputFilePath = "data.csv";
			outputFilePath = "defaultOutput.txt";
		}
		
		logger = new Logger( outputFilePath );
		
		List<Tweet> tweets = getTweetsFromFile( inputFilePath );
		//Set<String> stopWords = getStopWordsFromFile( stopWordsPath );
		
		TweetPreprocessor.of( tweets )
				.toLowerCase()
				.removeExtraSpaces()
				.processExpressivePunctuation()
				.removeIrrelevantPunctuation()
				//.removeStopWords( stopWords )
				.process();

		naiveBayesClassifierWithHoldout( tweets );
		
//		naiveBayesClassifierWithCrossValidation( tweets );
		
		long endOfProgram= System.currentTimeMillis();
		logger.log( "\nTotal time: %ds", (endOfProgram - begin) / 1000);
		logger.flush();
	}

	@SuppressWarnings("unused")
	private static Set<String> getStopWordsFromFile(String stopWordsPath) throws FileNotFoundException {
		final Scanner scanner = new Scanner( new File( stopWordsPath ) ); 
		final Set<String> stopWords = new HashSet<>();
		while( scanner.hasNextLine() ) {
			stopWords.add( scanner.nextLine() );
		}
		scanner.close();
		return Collections.unmodifiableSet( stopWords );
	}


	private static List<Tweet> getTweetsFromFile( final String path ) {
		logger.log( "Reading file from path %s", path );

		final long begin = System.currentTimeMillis();
		ParserCSV parser = new ParserCSV( new File( path ) );
		parser.readFile();
		long end = System.currentTimeMillis();
		
		logger.log( "Time to read file: %ds", (end-begin)/1000l );
		
		return parser.getTweets();
	}
	
	private static NaiveBayesClassifier getNaiveBayesClassifier( final List<Tweet> trainingList ) {
		Vocabulary vocabulary = new Vocabulary(trainingList);
		int happyCount = count( trainingList, true );
		int sadCount = count( trainingList, false );
		ClassesProbabilities probabilities = new ClassesProbabilities( vocabulary, happyCount, sadCount );
		logger.log( "\tHappy class probability: %.2f%%", probabilities.getHappyProbability() * 100);
		logger.log( "\tSad class probability: %.2f%%", probabilities.getSadProbability() * 100);
		return new NaiveBayesClassifier( probabilities );
	}
	
	private static void naiveBayesClassifierWithCrossValidation( final List<Tweet> tweets ) {
		logger.log("Running Naive Bayes Classifier with 10-fold cross-validation sampling technique");
		
		SamplingTechniques crossValidation = new CrossValidation( tweets );
		double[] acc = new double[10];

		for (int i = 0; i < 10; i++) {
			logger.log("Running k=%d iteration, using %d as test set.", i, i);
			final List<Tweet> trainingList = crossValidation.getTrainingList();
			final List<Tweet> testList = crossValidation.getTestList();
			
			NaiveBayesClassifier classifier = getNaiveBayesClassifier(trainingList);
			final List<Boolean> classifications = classifier.classify(testList);
			acc[i] = getAccuracy( classifications, testList );
			logger.log( "\tClassified with %.2f%% of accuracy", acc[i] * 100 );
			logger.log( "\tClassified with %.2f%% of true error", ( 1.0 - acc[i] ) * 100 );
			logConfusionMatrix( classifications, testList );
		}
		
		logger.log("\nFinal Accuracy: %.2f%%", getAccuracyAverage(acc) * 100);
		
	}

	private static void naiveBayesClassifierWithHoldout( final List<Tweet> tweets ) {
		logger.log("Running Naive Bayes Classifier with holdout sampling technique");
		SamplingTechniques holdOut = new HoldOut( tweets );
		
		List<Tweet> trainingList = holdOut.getTrainingList();
		List<Tweet> testList = holdOut.getTestList();
		
		logger.log("\tTotal size: %d", tweets.size());
		logger.log("\tTraining size: %d", trainingList.size());
		logger.log("\tTest size: %d", testList.size());

		NaiveBayesClassifier classifier = getNaiveBayesClassifier( trainingList );
		
		List<Boolean> classifications = classifier.classify( testList );
		
		final double accuracy = getAccuracy( classifications, testList );

		logger.log( "\tClassified with %.2f%% of accuracy", accuracy * 100 );
		logger.log( "\tClassified with %.2f%% of true error", ( 1.0 - accuracy ) * 100 );
		logConfusionMatrix( classifications, testList );
	}

	private static double getAccuracy( final List<Boolean> classifications, final List<Tweet> testList ) {
		double acc = 0d;
		for (int i = 0; i < testList.size(); i++) {
			if( classifications.get(i) == testList.get(i).isHappy() )
				acc++;
		}
		acc = acc / classifications.size();
		return acc;
	}
	
	private static double getAccuracyAverage( double[] acc ) {
		double average = 0;
		for (int i = 0; i < acc.length; i++) {
			average += acc[i];
		}
		return average / acc.length;
	}

	private static int count( final List<Tweet> trainingList, final boolean countHappy ) {
		int count = 0;
		for (Tweet tweet : trainingList) {
			if( tweet.isHappy() == countHappy )
				count++;
		}
		return count;
	}
	
	private static void logConfusionMatrix( final List<Boolean> classifications, final List<Tweet> tweets ) {
		double 	cell11 = 0d, // Was happy, classified as happy
				cell12 = 0d, // Was happy, classified as sad
				cell21 = 0d, // Was sad, classified as happy
				cell22 = 0d; // Was sad, classified as sad
		
		for( int i = 0; i < tweets.size(); i++ ) {
			final Tweet tweet = tweets.get( i );
			final Boolean classification = classifications.get( i );
			if( tweet.isHappy() ) {
				if( classification.booleanValue() ) 
					cell11++;
				else 
					cell12++;
			} else {
				if( !classification.booleanValue() )
					cell22++;
				else
					cell21++;
			}
		}
		
		final double row1Total = cell11 + cell12;
		final double row2Total = cell21 + cell22;
		
		cell11 /= row1Total;
		cell12 /= row1Total;
		cell21 /= row2Total;
		cell22 /= row2Total;
		
		logger.log("\t\tConfusion matrix:\n"
				+ "\t\tPredicted%12s%8s", "Happy", "Sad");
		logger.log("\t\tActual\n"
				+ "\t\tHappy%16.2f%%%4s%.2f%%\n"
				+ "\t\tSad%18.2f%%%4s%.2f%%", 
			cell11*100, "", cell12*100, 
			cell21*100, "", cell22*100 );
	}
}
