package main;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Realiza pre processamento sobre os textos dos {@link Tweet}s, antes que sejam submetidos ao
 * processamento para classificacao.
 */
public final class TweetPreprocessor {
	
	private static final Pattern USER_REFERENCE_PATTERN = Pattern.compile("@\\w+[\\d*\\w*]");
	private static final String USER_REFERENCE_REPLACER = "usrrf";
	
	private static final Pattern LINK_PATTERN = Pattern.compile("http://[\\w+\\d+]+\\.[\\w+\\d+]+(\\/[\\w+\\d+]+)*");
	private static final String LINK_REPLACER = "lnkrf";
	
	private static final Pattern HASHTAG_PATTERN = Pattern.compile("#[\\w+\\d+]+");
	private static final String HASHTAG_REPLACER = "hshtg";
	
	private static final Pattern PUNCTUATION_PATTERN = Pattern.compile(".*\\w([\\p{Punct}&&[^\\!\\?#@]])+\\w*");
	private static final Pattern PUNCTUATION_PATTERN_SEPARATOR = Pattern.compile("\\w\\p{Punct}");
	private static final Pattern EXPRESSIVE_PUNCTUATION = Pattern.compile("(\\p{Punct})(\\p{Punct})+");
	
	private static final Map<Pattern, String> replacerByPattern;

	static {
		final Map<Pattern, String> m = new LinkedHashMap<>();
		
		m.put( Pattern.compile("\\.{2,}"), " ...");
		m.put( Pattern.compile("\\b\\!{1}"), " !");
		m.put( Pattern.compile("\\!{2,5}"), " !!");
		m.put( Pattern.compile("\\!{3,}"), " !!!");
		m.put( Pattern.compile("\\?{2,5}"), " ??");
		m.put( Pattern.compile("\\?{3,}"), " ???");
		m.put( Pattern.compile("D{2,}"), "D");
		m.put( Pattern.compile("\\){2,}"), ")");
		m.put( Pattern.compile("\\({2,}"), "(");
		
		replacerByPattern = Collections.unmodifiableMap( m );
	}
	
	private static interface TweetPreprocessorAction {
		String preprocess( final String text );
	}
	
	private final List<Tweet> tweets;
	private final List<TweetPreprocessorAction> actions = new LinkedList<>();
	
	private TweetPreprocessor( final List<Tweet> tweets ) {
		this.tweets = tweets;
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
	public TweetPreprocessor processHashtags() {
		return addPatternReplacerAction( HASHTAG_PATTERN, HASHTAG_REPLACER );
	}
	
	public TweetPreprocessor removeIrrelevantPunctuation() {
		this.actions.add( new TweetPreprocessorAction() {
			
			private final StringBuilder result = new StringBuilder();
			private final StringBuilder newTerm = new StringBuilder();
			
			@Override
			public String preprocess( final String text ) {
				result.delete( 0, result.length() );
				final String[] terms = text.split("\\s");
				
				for( final String term : terms ) {
					newTerm.delete( 0, newTerm.length() );
					
					if( ! PUNCTUATION_PATTERN.matcher( term ).matches() || LINK_PATTERN.matcher(term ).matches()) {
						newTerm.append( term );
					} else {
						final Matcher matcher = PUNCTUATION_PATTERN_SEPARATOR.matcher( term );
						while( matcher.find() ) {
							final int secondHalfBeginIndex = matcher.end() - 1;
							final String firstHalf = term.substring( 0, secondHalfBeginIndex);
							final String secondHalf = term.substring( secondHalfBeginIndex, term.length() );
							
							if( ! EXPRESSIVE_PUNCTUATION.matcher( secondHalf ).matches() ) {
								newTerm.append( firstHalf );
								if( secondHalf.length() > 1 ) {
									newTerm.append( secondHalf.substring( 1, secondHalf.length() ));
								}
							} else {
								newTerm.append( term );
							}
						}
					}
					result.append( newTerm ).append(" ");
				}
				
				return result.toString();				
			}
		});
		
		return this;
	}

	public TweetPreprocessor processExpressivePunctuation() {
		this.actions.add( new TweetPreprocessorAction() {
			private final StringBuilder result = new StringBuilder();

			@Override
			public String preprocess( final String text ) {
				result.delete( 0, result.length() );
				final String[] terms = text.split("\\s");
				
				for( final String term : terms ) {
					String partial = term; 
					for( Map.Entry<Pattern,String> entry : replacerByPattern.entrySet() ) {
						partial = entry.getKey().matcher( partial ).replaceAll( entry.getValue() );
					}
					result.append( partial ).append(" ");
				}
				
				return result.toString();
			}
		});
		
		
		return this;
	}
	/**
	 * Aplica todas as acoes de processamento especificadas na lista original.
	 */
	public void process() {
		
		for (Tweet tweet : tweets) {
			String text = tweet.getText();
			for ( TweetPreprocessorAction action : this.actions ) {
				text = action.preprocess( text );
			}
			tweet.setText( text.trim() );
		}
	}
}
