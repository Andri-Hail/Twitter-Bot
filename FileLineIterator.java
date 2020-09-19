import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class FileLineIterator implements Iterator<String> {
	private BufferedReader br;
	private Iterator<String> iter;

	/**
	 *
	 * If an IOException is thrown by the BufferedReader or FileReader, then set
	 * next to null.
	 *
	 * @param filePath - the path to the CSV file to be turned to an Iterator
	 * @throws IllegalArgumentException if filePath is null or if the file doesn't
	 *                                  exist
	 */
	public FileLineIterator(String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException();
		} else {
			try {
				br = new BufferedReader(new FileReader(filePath));
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException();
			}
			iter = br.lines().iterator();
		}
	}

	/**
	 * Returns true if there are lines left to read in the file, and false
	 * otherwise.
	 * 
	 *
	 * @return a boolean indicating whether the FileLineIterator can produce another
	 *         line from the file
	 */
	@Override
	public boolean hasNext() {
		if (iter.hasNext()) {
			return true;
		} else {
			try {
				br.close();
			} catch (IOException e) {

			}
			return false;
		}
	}

	/**
	 * Returns the next line from the file, or throws a NoSuchElementException if
	 * there are no more strings left to return (i.e. hasNext() is false).
	 * 
	 * This method also advances the iterator in preparation for another invocation.
	 *
	 * @return the next line in the file
	 * @throws NoSuchElementException if there is no more data in the file
	 */
	@Override
	public String next() {
		if (hasNext() == false) {
			throw new NoSuchElementException();
		} else {
			return iter.next();
		}

	}
}
