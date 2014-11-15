package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ParserCSV {
	private File file;
	private List<Tweet> tweets;
	
	public ParserCSV(File file) {
		this.file = file;
		this.tweets = new ArrayList<Tweet>();
	}
	
	public void readFile() {
		try {
			Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name());
			sc.nextLine();
			while(sc.hasNextLine()) {
				tweets.add(parseText(sc.nextLine()));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Tweet parseText(String words) {
		String[] split = words.split(",");
		int id = Integer.parseInt(split[0]);
		boolean sentiment = split[1].equals("1");
		String text = split[3];
		if (split.length > 4) {
			for (int i = 4; i < split.length; i++) {
				text += "," + split[i];
			}
		}
		return new Tweet(id, sentiment, text.toLowerCase().trim());
	}

	public List<Tweet> getTweets() {
		return tweets;
	}
}
