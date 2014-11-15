import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
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
			Scanner sc = new Scanner(file);
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
		return new Tweet(id, sentiment, text.toLowerCase());
	}

	public List<Tweet> getTweets() {
		return tweets;
	}
	
	public static void main(String[] args) {
		HashMap<String,Integer> hashMap = new HashMap<String, Integer>();
		for(int i=0; i<3; i++){
			if(hashMap.get("fernanda")!= null) {
				hashMap.put("fernanda", hashMap.get("fernanda")+1);
			} else {
				hashMap.put("fernanda", 1);
			}
		}
		hashMap.put("caio", +1);
		System.out.println(hashMap.get("fernanda"));
	}
}
