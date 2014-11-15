package main;
import java.util.List;

public class HoldOut extends SamplingTechniques{
	private List<Tweet> trainingList;
	private List<Tweet> testList;
	
	public HoldOut(List<Tweet> tweets) {
		separateSets(tweets);
	}

	@Override
	public void separateSets(List<Tweet> tweets) {
		shuffle(tweets);
		
		int cut = (2 * tweets.size()) / 3;
		
		trainingList = tweets.subList(0, cut);
		testList = tweets.subList(cut, tweets.size());
	}

	@Override
	public List<Tweet> getTrainingList() {
		return trainingList;
	}

	@Override
	public List<Tweet> getTestList() {
		return testList;
	}
}
