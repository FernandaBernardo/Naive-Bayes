package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import main.ParserCSV;
import main.Tweet;

import org.junit.BeforeClass;
import org.junit.Test;

public class ParserCSVTest {
	
	private static ParserCSV parserCSV;
	private static List<Tweet> tweets;

	@BeforeClass
	public static void before() {
		parserCSV = new ParserCSV(new File("data.csv"));
		parserCSV.readFile();
		tweets = parserCSV.getTweets();
	}

	@Test
	public void firstTweet() {
		Tweet tweet = tweets.get(0);
		assertEquals("                    is so sad for my APL friend.............".trim().toLowerCase(), tweet.getText());
		assertEquals(1, tweet.getId());
		assertEquals(false, tweet.isHappy());
		
	}
	
	@Test
	public void anyTweet() {
		Tweet tweet = tweets.get(672785);
		assertEquals("Evil evil back pain. Hope puppy won't demand silly long walks today".trim().toLowerCase(), tweet.getText());
		assertEquals(672786, tweet.getId());
		assertEquals(false, tweet.isHappy());
	}

	@Test
	public void lastTweet() {
		Tweet tweet = tweets.get(tweets.size()-1);
		assertEquals("\"Zzzzzzzzzzzzzzzzzzz, I wish \"".trim().toLowerCase(), tweet.getText());
		assertEquals(1578627, tweet.getId());
		assertEquals(false, tweet.isHappy());
	}
	
	@Test
	public void numberOfTweets() {
		assertEquals(1578627, tweets.size());
	}
}

