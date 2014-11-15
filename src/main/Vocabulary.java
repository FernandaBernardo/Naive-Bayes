package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vocabulary {
	
	private Map<String, Integer> happyVocabulary;
	private Map<String, Integer> sadVocabulary;
	private int nHappy;
	private int nSad;

	public Vocabulary(List<Tweet> trainingList) {
		separateHappyAndSadVocabulary(trainingList);
		this.nHappy = sumOccurrences( happyVocabulary );
		this.nSad = sumOccurrences( sadVocabulary );
	}
	
	public void separateHappyAndSadVocabulary(List<Tweet> trainingList) {
		happyVocabulary = new HashMap<>();
		sadVocabulary = new HashMap<>();
		for (Tweet tweet : trainingList) {
			String[] text = tweet.getText().split(" ");
			if(tweet.isHappy()) {
				addToHashMap(happyVocabulary, text);
			} else {
				addToHashMap(sadVocabulary, text);
			}
		}
	}
	
	private int sumOccurrences( final Map<String, Integer> vocabulary ) {
		int sum = 0;
		for (Integer value : vocabulary.values()) {
			sum += value;
		}
		return sum;
	}
	
	private static void addToHashMap(Map<String, Integer> vocabulary, String[] text) {
		for (int i = 0; i<text.length; i++) {
			if(vocabulary.get(text[i])!= null) {
				vocabulary.put(text[i], vocabulary.get(text[i])+1);
			} else {
				vocabulary.put(text[i], 1);
			}
		}
	}
	
	public Map<String, Integer> getHappyVocabulary() {
		return happyVocabulary;
	}
	
	public Map<String, Integer> getSadVocabulary() {
		return sadVocabulary;
	}

	public int getTotalHappy() {
		return nHappy;
	}

	public int getTotalSad() {
		return nSad;
	}
}
