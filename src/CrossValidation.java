import java.util.List;


public class CrossValidation extends SamplingTechniques{
	private List<Tweet> trainingList;
	private List<Tweet> testList;
	
	public CrossValidation(List<Tweet> tweets) {
		separateSets(tweets);
	}
	
	@Override
	public void separateSets(List<Tweet> tweets) {
		//TODO
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
