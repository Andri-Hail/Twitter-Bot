import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TwitterBot {

	static final int MAX_TWEET_LENGTH = 280;
	static final String pathToTweets = "files/dog_feelings_tweets.csv";
	static final int tweetColumn = 2;
	static final String pathToOutputTweets = "files/generated_tweets.txt";

	
	MarkovChain mc;
	NumberGenerator ng;

	/**
	 * Given a column and a path to the csvFile, initializes the TwitterBot by
	 * training the MarkovChain with sentences sourced from that CSV file. Uses the
	 * RandomNumberGenerator().
	 *
	 * @param csvFile     - a path to a CSV file containing tweet data
	 * @param tweetColumn - the column in that CSV where the text of the tweet
	 *                    itself is stored
	 */
	public TwitterBot(String csvFile, int tweetColumn) {
		this(csvFile, tweetColumn, new RandomNumberGenerator());
	}

	/**
	 * Given a column and a path to the csvFile, initializes the TwitterBot by
	 * training the MarkovChain with all the sentences obtained as training data
	 * from that CSV file.
	 *
	 * @param csvFile     - a path to a CSV file containing tweet data
	 * @param tweetColumn - the column in that CSV where the text of the tweet
	 *                    itself is stored
	 * @param ng          - A NumberGenerator for the ng field, also to be passed to
	 *                    MarkovChain
	 */
	public TwitterBot(String csvFile, int tweetColumn, NumberGenerator ng) {
		mc = new MarkovChain(ng);
		this.ng = ng;
		List<List<String>> tweets = TweetParser.csvFileToTrainingData(csvFile, tweetColumn);
		Iterator<List<String>> iter = tweets.iterator();
		while (iter.hasNext()) {
			Iterator<String> iter2 = iter.next().iterator();
			while (iter2.hasNext()) {
				mc.train(iter2);
			}
		}
	}

	/**
	 * 
	 * @param stringsToWrite - A List of Strings to write to the file
	 * @param filePath       - the string containing the path to the file where the
	 *                       tweets should be written
	 * @param append         - a boolean indicating whether the new tweets should be
	 *                       appended to the current file or should overwrite its
	 *                       previous contents
	 */
	public void writeStringsToFile(List<String> stringsToWrite, String filePath, boolean append) {
		File file = Paths.get(filePath).toFile();
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(file, append));
			for (int i = 0; i < stringsToWrite.size(); i++) {
				br.write(stringsToWrite.get(i));
				br.newLine();
			}
			br.close();

		} catch (IOException e) {

		}

	}

	/**
	 * Generates tweets and writes them to a file.
	 * 
	 * @param numTweets   - the number of tweets that should be written
	 * @param tweetLength - the approximate length (in characters) of each tweet
	 * 
	 * @param filePath    - the path to a file to write the tweets to
	 * @param append      - a boolean indicating whether the new tweets should be
	 *                    appended to the current file or should overwrite its
	 *                    previous contents
	 */
	public void writeTweetsToFile(int numTweets, int tweetLength, String filePath, boolean append) {
		writeStringsToFile(generateTweets(numTweets, tweetLength), filePath, append);
	}

	
	 *
	 * @param length - The desired (approximate) length of the tweet (in characters)
	 *               to be produced
	 * @return a String representing a generated tweet
	 * @throws IllegalArgumentException if length is less than 1 or greater than
	 *                                  MAX_TWEET_LENGTH
	 */
	public String generateTweet(int length) {
		int i = 0;
		String tweet = "";
		if (length < 280 && length > 1) {
			mc.reset();
			tweet = mc.next();
			while (i <= length && tweet.length() <= 280) {
				if (mc.hasNext()) {
					tweet = tweet + " " + mc.next();
					i = tweet.length();
				} else {
					tweet = tweet + randomPunctuation();
					i = tweet.length();
					if (tweet.length() >= length || tweet.length() >= 280) {
						break;
					} else {
						mc.reset();
						tweet = tweet + " " + mc.next();
						i = tweet.length();
					}
				}
			}
			if (isPunctuated(tweet)) {
				return tweet;
			} else {
				tweet = tweet + randomPunctuation();
				return tweet;
			}

		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Generates a series of tweets using generateTweet().
	 *
	 * @param numTweets   - the number of tweets to generate
	 * @param tweetLength - the length that each generated tweet should be.
	 * @return a List of Strings where each element is a tweet
	 */
	public List<String> generateTweets(int numTweets, int tweetLength) {
		List<String> tweets = new ArrayList<String>();
		while (numTweets > 0) {
			tweets.add(generateTweet(tweetLength));
			numTweets--;
		}
		return tweets;
	}

	/**
	 * A helper function for providing a random punctuation String. Returns '.' 70%
	 * of the time and ';', '?', and '!' each 10% of the time.
	 * 
	 * @return a string containing just one punctuation character
	 */
	public String randomPunctuation() {
		char[] puncs = { ';', '?', '!' };
		int m = ng.next(10);
		if (m < puncs.length)
			return String.valueOf(puncs[m]);
		return ".";
	}

	/**
	 * A helper function to determine if a string ends in punctuation.
	 *
	 * @param s - an input string to check for punctuation
	 * @return true if the string s ends in punctuation
	 */
	public static boolean isPunctuated(String s) {
		if (s == null || s.equals("")) {
			return false;
		}
		char[] puncs = TweetParser.getPunctuation();
		for (char c : puncs) {
			if (s.charAt(s.length() - 1) == c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prints ten generated tweets to the console 
	 */
	public static void main(String args[]) {
		TwitterBot t = new TwitterBot(pathToTweets, tweetColumn);
		List<String> tweets = t.generateTweets(10, 140);
		for (String tweet : tweets) {
			System.out.println(tweet);
		}
		
	}

}
