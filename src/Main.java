import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		ParserCSV parser = new ParserCSV(new File("data.csv"));
		
		long begin = System.currentTimeMillis();
		
		parser.readFile();
		
		long end = System.currentTimeMillis();
		
		System.out.println("Time to read file: " + (end-begin));
		
		List<Tweet> tweets = parser.getTweets();
	}
}

