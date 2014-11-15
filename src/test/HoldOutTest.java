package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import main.HoldOut;
import main.ParserCSV;
import main.Tweet;

import org.junit.BeforeClass;
import org.junit.Test;

public class HoldOutTest {

	private static List<Tweet> tweets;
	private static HoldOut holdOut;
	private static List<Tweet> trainingList;
	private static List<Tweet> testList;

	@BeforeClass
	public static void before() {
		ParserCSV parserCSV = new ParserCSV(new File("data.csv"));
		parserCSV.readFile();
		tweets = parserCSV.getTweets();
		holdOut = new HoldOut(tweets);
		trainingList = holdOut.getTrainingList();
		testList = holdOut.getTestList();
	}
	
	@Test
	public void sizeTestList() {
		int totalSize = tweets.size();
		int testSize = totalSize/3;
		assertEquals(testSize, testList.size());
	}
	
	@Test
	public void sizeTrainingList() {
		int totalSize = tweets.size();
		int trainingSize = 2*totalSize/3;
		assertEquals(trainingSize, trainingList.size());
	}

}
