import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		ParserCSV parser = new ParserCSV(new File("data.csv"));
		
		long begin = System.currentTimeMillis();
		
		parser.readFile();
		
		long endofRead = System.currentTimeMillis();
		
		System.out.println("Time to read file: " + (endofRead-begin)/1000 + "s");
		
		List<Tweet> tweets = parser.getTweets();

		HoldOut holdOut = new HoldOut(tweets);
		
		List<Tweet> trainingList = holdOut.getTrainingList();
		List<Tweet> testList = holdOut.getTestList();
		
		System.out.println("Total size: " + tweets.size());
		System.out.println("Training size: " + trainingList.size());
		System.out.println("Test size: " + testList.size());
		
		Vocabulary vocabulary = new Vocabulary(trainingList);
		Map<String, Integer> happyVocabulary = vocabulary.getHappyVocabulary();
		Map<String, Integer> sadVocabulary = vocabulary.getSadVocabulary();

		long endOfProgram= System.currentTimeMillis();
		System.out.println("Total time: " + (endOfProgram - begin)/1000 + "s");
	}
}


