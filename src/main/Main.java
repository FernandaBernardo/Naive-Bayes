package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {
	
	private static long begin = System.currentTimeMillis();
	
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
		log( "Happy class probability: %.2f%%", probabilities.getHappyProbability() * 100);
		log( "Sad class probability: %.2f%%", probabilities.getSadProbability() * 100);
		return new NaiveBayesClassifier( probabilities );
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		final String path;
		
		if( args.length != 0 )
			path = args[0];
		else
			path = "data.csv";
		
		log("Running Naive Bayes Classifier with holdout sampling technique");
		List<Tweet> tweets = getTweetsFromFile( path );
		
		TweetPreprocessor.of( tweets )
				.removeAllPunctuation()
				.switchExtraSpaces()
				.process();
		
		HoldOut holdOut = new HoldOut( tweets );
		
		List<Tweet> trainingList = holdOut.getTrainingList();
		List<Tweet> testList = holdOut.getTestList();
		
		log("Total size: %d", tweets.size());
		log("Training size: %d", trainingList.size());
		log("Test size: %d", testList.size());

		NaiveBayesClassifier classifier = getNaiveBayesClassifier( trainingList );
		
		List<Boolean> classifications = classifier.classify( testList );
		
		final double accuracy = getAccuracy( classifications, testList );

		log( "Classified with %.2f%% of accuracy", accuracy*100 );
		
		log("Running Naive Bayes Classifier with cross-validation sampling technique");
		
		CrossValidation crossValidation = new CrossValidation(tweets);
		
		//TODO calcular média e erro padrão
		for (int i = 0; i < 10; i++) {
			trainingList = crossValidation.getTrainingList();
			testList = crossValidation.getTestList();
			
			log("Total size: %d", tweets.size());
			log("Training size: %d", trainingList.size());
			log("Test size: %d", testList.size());
			
			classifier = getNaiveBayesClassifier(trainingList);
			classifications = classifier.classify(testList);
		}
		
		long endOfProgram= System.currentTimeMillis();
		log( "\nTotal time: %ds", (endOfProgram - begin) / 1000);
	}

	private static double getAccuracy(final List<Boolean> classifications, final List<Tweet> testList) {
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
