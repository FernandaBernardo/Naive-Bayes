package test;

import static org.junit.Assert.assertEquals;







import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import main.Tweet;
import main.TweetPreprocessor;

import org.junit.Before;
import org.junit.Test;

public class TweetPreprocessorTest {

	private List<Tweet> tweets;
	private TweetPreprocessor subject;
	private Set<String> stopWords = Collections.unmodifiableSet( new HashSet<>( Arrays.asList( "over", "the" ) ) );
	
	@Before
	public void setUp() {
		tweets = new LinkedList<>();
		final Random random = new Random( System.nanoTime() ); 
		for( int id = 0 ; id < 30; id++ ) {
			tweets.add( new Tweet( 
					random.nextInt(), 
					random.nextBoolean(), 
					"Lorem ipsum dolor???? The quick brown =) fox jumps over the lazy dog!!!!!!" ) );
		}
		tweets = Collections.unmodifiableList( tweets );
		subject = TweetPreprocessor.of( tweets );
	}
	
	@Test
	public void shouldNotAddOrRemoveTweets() {
		int initialSize = tweets.size();
		subject
			.removeAllPunctuation()
			.removeExtraSpaces()
			.process();
		
		assertEquals( initialSize, tweets.size() );
	}
	
	@Test
	public void shouldSwitchWhiteSpaces() {
		String text = "       Sunny Again        Work Tomorrow   :-|       TV Tonight"; 
		final Tweet tweet = new Tweet( 1, true, text );
		final List<Tweet> sigleton = Collections.singletonList( tweet );
		TweetPreprocessor.of( sigleton )
			.removeExtraSpaces()
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
	
	@Test
	public void shouldRemoveOnlyIrrelevantPunctuations() {
		final String text1 = "awhhe man.... I'm completely useless rt now. "
				+ "Funny, all I can do is twitter. http://myloc.me/27HX What else can i do??????";
		final String text2 = "BoRinG   ): whats wrong with him??     Please tell me........   :-/";
		final Tweet tweet1 = new Tweet( 1, false, text1 );
		final Tweet tweet2 = new Tweet( 2, false, text2 );
		TweetPreprocessor.of( Arrays.asList( tweet1, tweet2 ) )
			.removeIrrelevantPunctuation()
			.process();
		
		final String expected1 = "awhhe man.... Im completely useless rt now "
				+ "Funny all I can do is twitter http://myloc.me/27HX What else can i do??????";
		assertEquals( expected1, tweet1.getText() );
		assertEquals( text2, tweet2.getText() );
	}
	
	@Test
	public void expressivePunctuationProcessing() {
		subject.processExpressivePunctuation().process();
		final Tweet actual = tweets.get( 0 );
		assertEquals( "Lorem ipsum dolor ?? The quick brown =) fox jumps over the lazy dog   !!!", actual.getText() );
	}
	
	@Test
	public void shouldRemoveStopWords() {
		subject.removeStopWords( stopWords ).process();
		final Tweet actual = tweets.get( 0 );
		assertEquals( "Lorem ipsum dolor???? quick brown =) fox jumps lazy dog!!!!!!", actual.getText() );
	}
	
}
