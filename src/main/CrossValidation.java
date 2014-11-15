package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CrossValidation implements SamplingTechniques{
	private int numberOfTestFold;
	private int numberOfTrainingFold;
	private List<Tweet>[] folds;
	
	public CrossValidation(List<Tweet> tweets) {
		separateSets(tweets);
		numberOfTestFold = 0;
		numberOfTrainingFold = 0;
	}
	
	@Override
	public void separateSets(List<Tweet> tweets) {
		Collections.shuffle(tweets);
		
		int sizeOfFolds = (tweets.size() / 10);
		folds = new List[10];
		for (int i = 0; i < 10; i++) {
			if(i == 9) {
				folds[i] = tweets.subList(i * sizeOfFolds, tweets.size());
			} else {
				folds[i] = tweets.subList(i * sizeOfFolds, ((i+1) * sizeOfFolds) - 1);
			}
		}
	}

	@Override
	public List<Tweet> getTrainingList() {
		List<Tweet> trainingFolds = new ArrayList<Tweet>();
		for (int i = 0; i < folds.length; i++) {
			if(i!= numberOfTrainingFold) {
				trainingFolds.addAll(folds[i]);
			}
		}
		numberOfTrainingFold++;
		return trainingFolds;
	}

	@Override
	public List<Tweet> getTestList() {
		return folds[numberOfTestFold++];
	}
}