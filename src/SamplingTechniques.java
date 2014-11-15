import java.util.Collections;
import java.util.List;


public abstract class SamplingTechniques {
	public abstract void separateSets(List<Tweet> tweets);
	public abstract List<Tweet> getTrainingList();
	public abstract List<Tweet> getTestList();
	
	public List<Tweet> shuffle(List<Tweet> tweets) {
		Collections.shuffle(tweets);
		return tweets;
	}
}
