package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public final class Main {
	
	private static long begin = System.currentTimeMillis();

	public static void main(String[] args) throws FileNotFoundException {
		final String path;
		
		if( args.length != 0 )
			path = args[0];
		else
			path = "data.csv";
		
		List<Tweet> tweets = getTweetsFromFile( path );
		
		TweetPreprocessor.of( tweets )
				//.removeAllPunctuation()
				.switchExtraSpaces()
				.process();

		naiveBayesClassifierWithHoldout( tweets );
		
		naiveBayesClassifierWithCrossValidation( tweets );
		
		long endOfProgram= System.currentTimeMillis();
		log( "\nTotal time: %ds", (endOfProgram - begin) / 1000);
	}

	
	private static List<Tweet> getTweetsFromFile( final String path ) {
		log( "Reading file from path %s", path );

		ParserCSV parser = new ParserCSV( new File( path ) );
		parser.readFile();
		long endOfRead = System.currentTimeMillis();
		
		log( "Time to read file: %ds", (endOfRead-begin)/1000l );
		
		return parser.getTweets();
	}
	
	private static NaiveBayesClassifier getNaiveBayesClassifier( final List<Tweet> trainingList ) {
		Vocabulary vocabulary = new Vocabulary(trainingList);
		int happyCount = count( trainingList, true );
		int sadCount = count( trainingList, false );
		ClassesProbabilities probabilities = new ClassesProbabilities( vocabulary, happyCount, sadCount );
		log( "\tHappy class probability: %.2f%%", probabilities.getHappyProbability() * 100);
		log( "\tSad class probability: %.2f%%", probabilities.getSadProbability() * 100);
		return new NaiveBayesClassifier( probabilities );
	}
	

	private static void naiveBayesClassifierWithCrossValidation( final List<Tweet> tweets ) {
		log("Running Naive Bayes Classifier with 10-fold cross-validation sampling technique");
		
		SamplingTechniques crossValidation = new CrossValidation( tweets );
		double[] acc = new double[10];

		for (int i = 0; i < 10; i++) {
			log("Running k=%d iteration, using %d as test set.", i, i);
			final List<Tweet> trainingList = crossValidation.getTrainingList();
			final List<Tweet> testList = crossValidation.getTestList();
			
			NaiveBayesClassifier classifier = getNaiveBayesClassifier(trainingList);
			final List<Boolean> classifications = classifier.classify(testList);
			acc[i] = getAccuracy( classifications, testList );
			log( "\tClassified with %.2f%% of accuracy", acc[i] * 100 );
		}
		
		log("\nFinal Accuracy: %.2f%%", getAccuracyAverage(acc) * 100);
		
	}

	private static double getAccuracyAverage(double[] acc) {
		double average = 0;
		for (int i = 0; i < acc.length; i++) {
			average += acc[i];
		}
		return (average / acc.length);
	}


	private static void naiveBayesClassifierWithHoldout( final List<Tweet> tweets ) {
		log("Running Naive Bayes Classifier with holdout sampling technique");
		SamplingTechniques holdOut = new HoldOut( tweets );
		
		List<Tweet> trainingList = holdOut.getTrainingList();
		List<Tweet> testList = holdOut.getTestList();
		
		log("\tTotal size: %d", tweets.size());
		log("\tTraining size: %d", trainingList.size());
		log("\tTest size: %d", testList.size());

		NaiveBayesClassifier classifier = getNaiveBayesClassifier( trainingList );
		
		List<Boolean> classifications = classifier.classify( testList );
		
		final double accuracy = getAccuracy( classifications, testList );

		log( "\tClassified with %.2f%% of accuracy", accuracy*100 );
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

	private static int count( final List<Tweet> trainingList, final boolean countHappy ) {
		int count = 0;
		for (Tweet tweet : trainingList) {
			if( tweet.isHappy() == countHappy )
				count++;
		}
		return count;
	}

	private static void log( final String message, final Object... args ){
		System.out.printf(message+"\n", args);
	}
}
