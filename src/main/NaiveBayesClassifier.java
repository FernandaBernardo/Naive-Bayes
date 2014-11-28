package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa um classificador bayesiano ingenuo
 * com base nas especificacoes em
 * 
 * "Parte 2: Implementacao do Classificador Naive Bayes" 
 *
 */
final class NaiveBayesClassifier {
	
	private final ClassesProbabilities probabilities;
	
	public NaiveBayesClassifier( final ClassesProbabilities probabilities ) {
		this.probabilities = probabilities;
	}

	/**
	 * Classifica um {@link Tweet} como "feliz" ou "triste";
	 *   
	 * @param tweet tweet a ser classificado
	 * @return <code>true</code> se o tweet for classificado como 
	 * "feliz" ou <code>false</code>, caso contrario
	 */
	public boolean classify( final Tweet tweet ){
		final String[] text = tweet.getText().split(" ");
		// Usa log para evitar underflow
		double tweetHappyProbability = Math.log10( probabilities.getHappyProbability() );
		double tweetSadProbability = Math.log10( probabilities.getSadProbability() );
		
		for (String word : text) {
			tweetHappyProbability += Math.log10( probabilities.getHappyConditionalProbability( word ) );
			tweetSadProbability += Math.log10( probabilities.getSadConditionalProbability( word ) ); 
		}
		
		return tweetHappyProbability >= tweetSadProbability;
	}
	
	/**
	 * Dada uma lista de {@link Tweet}s, retorna uma lista com as 
	 * classificacoes feitas para cada tweet. O i-esimo tweet em 
	 * <code>tweets</code> tem a i-esima classificacao da lista retornada.
	 */
	public List<Boolean> classify( final List<Tweet> tweets ){
		final List<Boolean> classifications = new ArrayList<>();
		for ( Tweet tweet : tweets ) {
			classifications.add( classify( tweet ) ? Boolean.TRUE : Boolean.FALSE );
		}
		
		return Collections.unmodifiableList( classifications );
	}
}
