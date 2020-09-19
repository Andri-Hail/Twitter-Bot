import java.util.LinkedList;
import java.util.List;

/**
 * TweetParser.csvFileToTrainingData() takes in a CSV file that contains tweets
 * and iterates through the file, one tweet at a time, removing parts of the
 * tweets that would be bad inputs to MarkovChain (for example, a URL). It then
 * parses tweets into sentences and returns those sentences as lists of
 * cleaned-up words.
 *
 */
public class TweetParser {

	
	private static final String BADWORD_REGEX = ".*[\\W&&[^']].*";
	private static final String URL_REGEX = "\\bhttp\\S*";

	/**
	 * 
	 * 
	 * @param s - a String from which URL-like words should be removed
	 * @return s where each "URL-like" string has been deleted
	 */
	static String removeURLs(String s) {
		return s.replaceAll(URL_REGEX, "");
	}

	/**
	 * 
	 * Cleans a word by removing leading and trailing whitespace and converting it
	 * to lower case. If the word matches the BADWORD_REGEX or is the empty String,
	 * returns null instead.
	 * 
	 * @param word - a (non-null) String to clean
	 * @return - a trimmed, lowercase version of the word if it contains no illegal
	 *         characters and is not empty, and null otherwise.
	 */
	static String cleanWord(String word) {
		String cleaned = word.trim().toLowerCase();
		if (cleaned.matches(BADWORD_REGEX) || cleaned.isEmpty())
			return null;
		return cleaned;
	}

	/**
	 * Valid punctuation marks.
	 */
	static final char[] puncs = new char[] { '.', '?', '!', ';' };

	/**
	 * 
	 * @return an array containing the punctuation marks used by the parser.
	 */
	public static char[] getPunctuation() {
		return puncs.clone();
	}

	/**
	 *
	 * Given a string, replaces all of the punctuation with periods.
	 *
	 * @param tweet - a String representing a tweet
	 * @return A String with all of the punctuation replaced with periods
	 */
	static String replacePunctuation(String tweet) {
		for (char c : puncs) {
			tweet = tweet.replace(c, '.');
		}
		return tweet;
	}

	/**
	 *
	 * Given a tweet, splits the tweet into sentences (without end punctuation) and
	 * inserts each sentence into a list.
	 *
	 *
	 * @param tweet - a String representing a tweet
	 * @return A List of Strings where each String is a (non-empty) sentence from
	 *         the tweet
	 */
	static List<String> sentenceSplit(String tweet) {
		List<String> sentences = new LinkedList<String>();
		for (String sentence : replacePunctuation(tweet).split("\\.")) {
			sentence = sentence.trim();
			if (!sentence.equals("")) {
				sentences.add(sentence);
			}
		}
		return sentences;
	}

	/**
	 *
	 * @param csvLine   - a line extracted from a CSV file
	 * @param csvColumn - the column of the line whose contents ought to be returned
	 * @return the portion of csvLine corresponding to the column of csvColumn. If
	 *         the csvLine is null or has no appropriate csvColumn, return null
	 */
	static String extractColumn(String csvLine, int csvColumn) {
		if (csvLine == null) {
			return null;
		} else {
			String[] lineSplit = csvLine.split(",");
			if (lineSplit.length <= csvColumn - 1 || csvColumn < 0) {
				return null;
			} else {
				return lineSplit[csvColumn];

			}
		}
	}

	/**
	 * Splits a String representing a sentence into a sequence of words, filtering
	 * out any "bad" words from the sentence.
	 * 
	 * 
	 * @param sentence - a String representing one sentence from a tweet
	 * @return a (non-null) list of clean words in the order they appear in the
	 *         sentence. Any "bad" words are just dropped.
	 */

	static List<String> parseAndCleanSentence(String sentence) {

		String[] sentenceSplit = sentence.split(" ");
		System.out.println(sentenceSplit.toString());

		List<String> cleanedWords = new LinkedList<String>();
		for (int i = 0; i < sentenceSplit.length; i++) {
			for (char c : puncs) {
				sentenceSplit[i] = sentenceSplit[i].replace(c, ' ');
			}
			if (cleanWord(sentenceSplit[i]) != null) {
				cleanedWords.add(cleanWord(sentenceSplit[i]));
			}
		}
		return cleanedWords;
	}

	/**
	 * Processes a tweet in to a list of sentences, where each sentence is itself a
	 * (non-empty) list of cleaned words. Before breaking up the tweet into
	 * sentences, this method uses removeURLs to sanitize the tweet.
	 * 
	 * 
	 * @param tweet - a String that will be split into sentences, each of which is
	 *              cleaned as described above (assumed to be non-null)
	 * 
	 * @return a (non-null) list of sentences, each of which is a (non-empty)
	 *         sequence of clean words drawn from the tweet.
	 */

	static List<List<String>> parseAndCleanTweet(String tweet) {
		String noURLS = removeURLs(tweet);
		List<String> sentenceSplit = sentenceSplit(noURLS);

		List<List<String>> cleanedSentences = new LinkedList<List<String>>();
		for (int i = 0; i < sentenceSplit.size(); i++) {
			cleanedSentences.add(parseAndCleanSentence(sentenceSplit.get(i)));

		}
		return cleanedSentences;
	}

	/**
	 * 
	 * @param pathToCSVFile - a String representing a path to a CSV file containing
	 *                      tweets
	 * @param tweetColumn   - the number of the column in the CSV file that contains
	 *                      the tweet
	 * @return a List of tweet Strings, none of which are null (but that are not yet
	 *         cleaned)
	 * 
	 * @throws IllegalArgumentException if pathToCSVFile is null or if the file
	 *                                  doesn't exist
	 * 
	 * @param pathToCSVFile
	 * @param tweetColumn
	 * @return
	 */
	static List<String> csvFileToTweets(String pathToCSVFile, int tweetColumn) {
		FileLineIterator f = new FileLineIterator(pathToCSVFile);
		List<String> tweets = new LinkedList<String>();
		while (f.hasNext()) {
			String y = f.next();
			if (extractColumn(y, tweetColumn) != null) {
				tweets.add(extractColumn(y, tweetColumn));
			}
		}
		return tweets;
	}

	/**
	 * 
	 * @param pathToCSVFile - a String representing a path to a CSV file containing
	 *                      tweets
	 * @param tweetColumn   - the number of the column in the CSV file that contains
	 *                      the tweet
	 * @return a list of training data examples
	 * 
	 * @throws IllegalArgumentException if pathToCSVFile is null or if the file
	 *                                  doesn't exist
	 */
	public static List<List<String>> csvFileToTrainingData(String pathToCSVFile, int tweetColumn) {
		List<String> tweets = csvFileToTweets(pathToCSVFile, tweetColumn);
		List<List<String>> cleanedSentences = new LinkedList<List<String>>();
		for (int i = 0; i < tweets.size(); i++) {
			cleanedSentences.add(parseAndCleanSentence(tweets.get(i)));
		}
		return cleanedSentences;
	}

}
