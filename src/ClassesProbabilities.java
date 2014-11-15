import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Calcula probabilidades das classes "feliz" e "triste", considerando 
 * estimativa de Laplace.
 * 
 */
final class ClassesProbabilities {
	
	private final Map<String, Integer> happyVocabulary;
	private final Map<String, Integer> sadVocabulary;
	private final int vocabularyCardinality;
	private final int nHappy;
	private final int nSad;
	private final double happyProbability;
	private final double sadProbability;

	/**
	 * Construtor. 
	 * <p>
	 * A partir dos argumentos, calcula dados necessarios para 
	 * calcular probabilidades de palavras pertencerem a uma das classes.
	 *  
	 * @param vocabulary vocabulario gerado da leitura do arquivo
	 * @param happyCount numero de tweets pertencentes a classe "feliz"
	 * @param sadCount numero de tweets pertencentes a classe "triste"
	 */
	public ClassesProbabilities( final Vocabulary vocabulary, final int happyCount, int sadCount ) {
		this.happyVocabulary = vocabulary.getHappyVocabulary();
		this.sadVocabulary = vocabulary.getSadVocabulary();
		
		this.nHappy = vocabulary.getTotalHappy();
		this.nSad = vocabulary.getTotalSad();
		
		final Set<String> set = new HashSet<>( happyVocabulary.keySet() ); 
		set.addAll( sadVocabulary.keySet() );
		this.vocabularyCardinality = set.size();
		
		double totalCount = happyCount + sadCount;
		this.happyProbability = happyCount / totalCount;
		this.sadProbability = sadCount / totalCount;
	}

	private double getConditionalProbability(final String word, final int nClass, final Map<String, Integer> vocabulary ) {
		final Integer value = vocabulary.get(word);
		// Formula da estimativa de laplace
		final double nWord = value == null ? 1 : value + 1;
		return nWord / ( nClass + vocabularyCardinality );
	}
	
	public double getHappyConditionalProbability( final String word ) {
		return getConditionalProbability( word, nHappy, happyVocabulary );
	}
	
	public double getSadConditionalProbability( final String word ) {
		return getConditionalProbability( word, nSad, sadVocabulary );
	}
	
	public double getHappyProbability() {
		return this.happyProbability;
	}
	
	public double getSadProbability() {
		return this.sadProbability;
	}
}
