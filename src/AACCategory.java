import java.util.NoSuchElementException;

import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.NullKeyException;

/**
 * Represents the mappings for a single category of items that should
 * be displayed
 * 
 * @author Catie Baker
 * @author David William Stroud
 *
 */
public class AACCategory implements AACPage {
	/**
	 * This is the name of this category.
	 */
	private String name;
	/**
	 * This is the mapping of image locations in this category to text.
	 */
	private AssociativeArray<String, String> imageLocs = new AssociativeArray<>();
	
	/**
	 * Creates a new empty category with the given name
	 * @param name1 the name of the category
	 */
	public AACCategory(String name1) {
		this.name = name1;
	}
	
	/**
	 * Adds the image location, text pairing to the category
	 * @param imageLoc the location of the image
	 * @param text the text that image should speak
	 */
	public void addItem(String imageLoc, String text) {
		try {
			imageLocs.set(imageLoc, text);
		} catch (NullKeyException err) {
			// This should never happen, so we will fail silently.
		}
	}

	/**
	 * Returns an array of all the images in the category
	 * @return the array of image locations; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		return this.imageLocs.getKeys("");
	}

	/**
	 * Returns the name of the category
	 * @return the name of the category
	 */
	public String getCategory() {
		return this.name;
	}

	/**
	 * Returns the text associated with the given image in this category
	 * @param imageLoc the location of the image
	 * @return the text associated with the image
	 * @throws NoSuchElementException if the image provided is not in the current
	 * 		   category
	 */
	public String select(String imageLoc) throws NoSuchElementException {
		try {
			return this.imageLocs.get(imageLoc);
		} catch (Exception err) {
			throw new NoSuchElementException("No such image: " + imageLoc);
		}
	}

	/**
	 * Determines if the provided images is stored in the category
	 * @param imageLoc the location of the category
	 * @return true if it is in the category, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.imageLocs.hasKey(imageLoc);
	}
}
