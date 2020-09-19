import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;


public class MarkovChain implements Iterator<String> {
	final NumberGenerator ng;
	final Map<String, ProbabilityDistribution<String>> chain;
	final ProbabilityDistribution<String> startWords;

	String next;

	
	public MarkovChain() {
		this(new RandomNumberGenerator());
	}

	/**
	 *
	 * @param ng - A (non-null) NumberGenerator used to walk through the MarkovChain
	 */
	public MarkovChain(NumberGenerator ng) {
		if (ng == null) {
			throw new IllegalArgumentException("NumberGenerator input cannot be null");
		}
		this.chain = new TreeMap<String, ProbabilityDistribution<String>>();
		this.ng = ng;
		this.startWords = new ProbabilityDistribution<String>();
		reset();
	}

	/**
	 * Adds a bigram to the Markov Chain dictionary. 
	 *
	 * @param first  - The first word of the Bigram (should not be null)
	 * @param second - The second word of the Bigram
	 * @throws IllegalArgumentException if the first parameter is null.
	 */
	void addBigram(String first, String second) {
		if (first != null) {
			if (chain.containsKey(first)) {
				chain.get(first).record(second);
			} else {
				ProbabilityDistribution p = new ProbabilityDistribution();
				p.record(second);
				chain.put(first, p);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Adds a sentence's training data to the MarkovChain frequency information.
	 *
	 *
	 * @param sentence - an iterator representing one sentence of training data
	 * @throws IllegalArgumentException if the sentence Iterator is null
	 */
	public void train(Iterator<String> sentence) {
		if (sentence != null) {
			if (sentence.hasNext() == true) {
				String first = sentence.next();
				startWords.record(first);
				while (sentence.hasNext()) {
					String second = sentence.next();
					addBigram(first, second);
					first = second;
				}
				addBigram(first, null);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns the ProbabilityDistribution for a given token. Returns null if none
	 * exists.
	 *
	 * @param token - the token for which the ProbabilityDistribution is sought
	 * @return a ProbabilityDistribution or null
	 */
	ProbabilityDistribution<String> get(String token) {
		return chain.get(token);
	}

	/**
	 *
	 * @param start - the element that will be the first word in a walk on the
	 *              Markov Chain.
	 */
	public void reset(String start) {
		if (startWords.getTotal() != 0 && start != null) {
			next = start;
		}
	}

	/**
	 *
	 * Sets up the Iterator functionality with a random start word such that the
	 * MarkovChain will now move along a walk beginning with that start word.
	 *
	 */
	public void reset() {
		if (startWords.getTotal() == 0) {
			reset(null);
		} else {
			reset(startWords.pick(ng));
		}
	}

	/**

	 *
	 * @return true if next() will return a String and false otherwise
	 */
	@Override
	public boolean hasNext() {
		if (next == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @return the next word in the MarkovChain (chosen at random via the number
	 *         generator if it is a successor)
	 * @throws NoSuchElementException if there are no more words on the walk through
	 *                                the chain.
	 */
	public String getNext() {
		return next;
	}

	@Override
	public String next() {
		if (next != null) {
			String output = next;
			next = chain.get(getNext()).pick(ng);
			return output;
		} else {
			throw new NoSuchElementException();
		}

	}

}
