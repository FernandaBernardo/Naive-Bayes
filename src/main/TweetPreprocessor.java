package main;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Realiza pre processamento sobre os textos dos {@link Tweet}s, antes que sejam submetidos ao
 * processamento para classificação.
 *
 */
public final class TweetPreprocessor {
	
	private static Pattern USER_REFERENCE_PATTERN = Pattern.compile("@\\w+[\\d*\\w*]");
	private static String USER_REFERENCE_REPLACER = "usrrf";
	
	private static Pattern LINK_PATTERN = Pattern.compile("http://[\\w+\\d+]+\\.[\\w+\\d+]+(\\/[\\w+\\d+]+)*");
	private static String LINK_REPLACER = "lnkrf";
	
	private static Pattern HASHTAG_PATTERN = Pattern.compile("#[\\w+\\d+]+");
	private static String HASHTAG_REPLACER = "hshtg";
	
	private static interface TweetPreprocessorAction {
		String preprocess(final String text);
	}
	
	private final List<Tweet> originalTweets;
	private final List<TweetPreprocessorAction> actions = new LinkedList<>();
	
	private TweetPreprocessor( final List<Tweet> tweets ) {
		this.originalTweets = tweets;
	}

	/**
	 * Constroi um {@link TweetPreprocessor}. Nao tira uma copia dos tweets do argumento,
	 * para evitar {@link OutOfMemoryError}. Dessa forma, faz as alteracoes no texto da
	 * lista de {@link Tweet}s do argumento.
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
	
	/**
	 * Converte todo texto para palavras em letras minusculas.
	 */
	public TweetPreprocessor toLowerCase() {
		this.actions.add( new TweetPreprocessorAction(){
			@Override
			public String preprocess(String text) {
				return text.toLowerCase();
			}
		});
		
		return this;
	}
	
	private TweetPreprocessor addPatternReplacerAction( final Pattern pattern, final String replacer ) {
		this.actions.add( new TweetPreprocessorAction() {
			
			@Override
			public String preprocess(String text) {
				return pattern.matcher(text).replaceAll( replacer );
			}
		});
		return this;
	}

	/**
	 * Substitui referencias a outros usuarios por um marcador de 
	 * ocorrencias, e.g, "@jesusCristo" e "@espiritoSanto" seriam 
	 * substituidos ambos por "usrf". Esse processamento objetiva 
	 * igualar todos esses termos.
	 */
	public TweetPreprocessor processUserReferences() {
		return addPatternReplacerAction( USER_REFERENCE_PATTERN, USER_REFERENCE_REPLACER );
	}
	
	/**
	 * Substitui urls por um marcador de ocorrencias, e.g, http://bit.ly/abc123
	 * e http://tumblr.com/efg456 seriam substituidos ambos por "lnkrf". 
	 * Esse processamento objetiva igualar todos esses termos.
	 */
	public TweetPreprocessor processLinks() {
		return addPatternReplacerAction( LINK_PATTERN, LINK_REPLACER );
	}
	
	/**
	 * Substitui termos precedidos por <i>hashtag</i> por um marcador 
	 * de ocorrencias, e.g, #eachusp e #chupamack seriam substituidos 
	 * ambos por "hshtg". Esse processamento objetiva igualar todos esses
	 * termos.
	 */
	public TweetPreprocessor processHashtags(){
		return addPatternReplacerAction( HASHTAG_PATTERN, HASHTAG_REPLACER );
	}
	

	/**
	 * Aplica todas as acoes de processamento especificadas na lista original.
	 */
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
