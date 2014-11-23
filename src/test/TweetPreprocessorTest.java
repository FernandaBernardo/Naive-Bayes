package test;

import static org.junit.Assert.assertEquals;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import main.Tweet;
import main.TweetPreprocessor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TweetPreprocessorTest {

	private List<Tweet> tweets;
	
	@Before
	public void setUp() {
		tweets = new LinkedList<>();
		final Random random = new Random( System.nanoTime() ); 
		for( int id = 0 ; id < 30; id++ ) {
			tweets.add( new Tweet( 
					random.nextInt(), 
					random.nextBoolean(), 
					"Lorem ipsum dolor" ) );
		}
		tweets = Collections.unmodifiableList( tweets );
	}
	
	@Test
	public void shouldNotAddOrRemoveTweets() {
		int initialSize = tweets.size();
		TweetPreprocessor.of( tweets )
				.removeAllPunctuation()
				.switchExtraSpaces()
				.process();
		
		assertEquals( initialSize, tweets.size() );
	}
	
	@Test
	public void shouldSwitchWhiteSpaces() {
		String text = "       Sunny Again        Work Tomorrow   :-|       TV Tonight"; 
		final Tweet tweet = new Tweet( 1, true, text );
		final List<Tweet> sigleton = Collections.singletonList( tweet );
		TweetPreprocessor.of( sigleton )
			.switchExtraSpaces()
			.process() ;
		assertEquals( "Sunny Again Work Tomorrow :-| TV Tonight", tweet.getText() );
	}
	
	@Test
	public void shouldRemoveAllPunctuations(){
		String text = "(: !!!!!! - so i wrote something last week.... and i got a call from someone in the new york office... http://tumblr.com/xcn21w6o7"; 
		final Tweet tweet = new Tweet( 1, true, text );
		final List<Tweet> sigleton = Collections.singletonList( tweet );
		TweetPreprocessor.of( sigleton )
			.removeAllPunctuation()
			.process();
		assertEquals( "so i wrote something last week     and i got a call from someone in the new york office    http   tumblr com xcn21w6o7", tweet.getText() );
	}
	
	@Ignore("Not yet implemented")
	@Test
	public void shouldSmartlyRemoveAllPunctuations(){
		String text = "(: !!!!!! - so i wrote something last week. and i got a call from someone in the new york office at 18:32... http://tumblr.com/xcn21w6o7"; 
		final Tweet tweet = new Tweet( 1, true, text );
		final List<Tweet> sigleton = Collections.singletonList( tweet );
		TweetPreprocessor.of( sigleton )
			.smartRemovePunctuation()
			.process();
		assertEquals( "(: ! so i wrote something last week  and i got a call from someone in the new york office at 18:32    http://tumblr.com/xcn21w6o7", tweet.getText() );
	}
}
