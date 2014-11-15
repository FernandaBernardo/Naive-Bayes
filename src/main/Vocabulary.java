package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vocabulary {
	private HashMap<String, Integer> happyVocabulary;
	private HashMap<String, Integer> sadVocabulary;

	public Vocabulary(List<Tweet> trainingList) {
		separateHappyAndSadVocabulary(trainingList);
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
	
	private static void addToHashMap(Map<String, Integer> vocabulary, String[] text) {
		for (int i = 0; i<text.length; i++) {
			if(vocabulary.get(text[i])!= null) {
				vocabulary.put(text[i], vocabulary.get(text[i])+1);
			} else {
				vocabulary.put(text[i], 1);
			}
		}
	}
	
	public HashMap<String, Integer> getHappyVocabulary() {
		return happyVocabulary;
	}
	
	public HashMap<String, Integer> getSadVocabulary() {
		return sadVocabulary;
	}
}
