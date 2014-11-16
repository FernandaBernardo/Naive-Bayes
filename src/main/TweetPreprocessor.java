package main;

import java.util.LinkedList;
import java.util.List;

/**
 * Realiza pre processamento sobre os textos dos {@link Tweet}s, antes que sejam submetidos ao
 * processamento para classificação.
 *
 */
public final class TweetPreprocessor {
	
	private static interface TweetPreprocessorAction {
		String preprocess(final String text);
	}
	
	private final List<Tweet> originalTweets;
	private final List<TweetPreprocessorAction> actions = new LinkedList<>();
	
	private TweetPreprocessor( final List<Tweet> tweets ) {
		this.originalTweets = tweets;
	}

	/**
	 * Constrói um {@link TweetPreprocessor}. Não tira uma cópia dos tweets do argumento,
	 * para evitar {@link OutOfMemoryError}. Dessa forma, faz as alterações no texto da
	 * lista do argumento.
	 * 
	 */
	public static TweetPreprocessor of(final List<Tweet> tweets  ) {
		return new TweetPreprocessor( tweets );
	}
	
	public TweetPreprocessor smartRemovePunctuation() {
		this.actions.add( new TweetPreprocessorAction() {
			
			@Override
			public String preprocess(String text) {
				return StringUtils.smartRemovePunctuation( text );
			}
		}); 
				
		return this;
	}

	/**
	 * Remove toda pontuação como !;?#$, dentre outros.
	 */
	public TweetPreprocessor removeAllPunctuation() {
		this.actions.add( new TweetPreprocessorAction() {
			
			@Override
			public String preprocess(String text) {
				return StringUtils.removeAllPunctuation( text );
			}
		}); 
				
		return this;
	}
	
	/**
	 * Substitui múltiplos espaços em branco por um único.
	 */
	public TweetPreprocessor switchExtraSpaces() {
		this.actions.add( new TweetPreprocessorAction() {
			
			@Override
			public String preprocess(String text) {
				return StringUtils.removeAllInternalExtraSpaces( text );
			}
		}); 
				
		return this;
	}

	public void process() {
		
		for (Tweet tweet : originalTweets) {
			String text = tweet.getText();
			for ( TweetPreprocessorAction action : this.actions ) {
				text = action.preprocess( text );
			}
			tweet.setText( text.trim() );
		}
	}
}
