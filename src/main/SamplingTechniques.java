package main;
import java.util.List;

public interface SamplingTechniques {
	public abstract void separateSets(List<Tweet> tweets);
	public abstract List<Tweet> getTrainingList();
	public abstract List<Tweet> getTestList();
}
