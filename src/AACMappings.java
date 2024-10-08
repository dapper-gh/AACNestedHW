import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker
 * @author David William Stroud
 *
 */
public class AACMappings implements AACPage {
	/**
	 * This is the mapping of category names to AACCategory instances for this mapping.
	 */
	private final AssociativeArray<String, AACCategory> categories = new AssociativeArray<String, AACCategory>();
	/**
	 * This is the category representing the homepage.
	 */
	private final AACCategory homepage = new AACCategory("");
	/**
	 * This is the name of the category that is currently selected.
	 */
	private String currentCategory = "";

	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));

			AACCategory category = null;
			String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(" ");

				String firstPart = words[0];
				String[] secondPartWords = new String[words.length - 1];
				System.arraycopy(words, 1, secondPartWords, 0, secondPartWords.length);
				String secondPart = String.join(" ", secondPartWords);

				if (line.startsWith(">")) {
					category.addItem(firstPart.substring(1), secondPart);
				} else {
					category = new AACCategory(secondPart);

					try {
						this.categories.set(secondPart, category);
						this.homepage.addItem(firstPart, secondPart);
					} catch (NullKeyException err) {
						// This should never happen, since there is no opportunity for firstPart to be null.
					}
				}
			}
		} catch (IOException err) {
			// Nothing we can do, so fail silently.
		}
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) throws NoSuchElementException {
		try {
			AACCategory currentCategory = this.categories.get(this.currentCategory);
			if (currentCategory.hasImage(imageLoc)) {
				return currentCategory.select(imageLoc);
			}
		} catch (KeyNotFoundException err) {
			// This should never happen, since currentCategoryLocation is always a valid key.
		}

		if (!this.hasImage(imageLoc)) {
			throw new NoSuchElementException("No such image location: " + imageLoc);
		}

		this.currentCategory = this.homepage.select(imageLoc);

		return "";
	}
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		return this.getCurrentCategory().getImageLocs();
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.currentCategory = "";
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		try {
			FileWriter writer = new FileWriter(filename);

			for (String imageLoc : this.homepage.getImageLocs()) {
				String name = this.homepage.select(imageLoc);

				writer.append(imageLoc);
				writer.append(' ');
				writer.append(name);
				writer.append('\n');

				try {
					AACCategory category = this.categories.get(name);
					for (String itemImageLoc : category.getImageLocs()) {
						writer.append('>');
						writer.append(itemImageLoc);
						writer.append(' ');
						writer.append(category.select(itemImageLoc));
						writer.append('\n');
					}
				} catch (KeyNotFoundException err) {
					// This should never happen, so we will fail silently.
				}
			}

			writer.flush();
			writer.close();
		} catch (IOException err) {
			// There is nothing we can do, so we must fail silently.
		}
	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		AACCategory currentCategory = this.getCurrentCategory();
		currentCategory.addItem(imageLoc, text);
		if (this.currentCategory.isEmpty()) {
			try {
				this.categories.set(text, new AACCategory(text));
			} catch (NullKeyException err) {
				// This should never happen, so we will fail silently.
			}
		}
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return this.currentCategory;
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.getCurrentCategory().hasImage(imageLoc);
	}

	/**
	 * This method returns the currently selected category.
	 * @return The currently selected category.
	 */
	private AACCategory getCurrentCategory() {
		if (this.currentCategory.isEmpty()) {
			return this.homepage;
		}

		try {
			return this.categories.get(this.currentCategory);
		} catch (KeyNotFoundException err) {
			// This should never happen, since currentCategoryLocation is always a valid key.
			// But, we have to return something, so we will return the homepage.

			return this.homepage;
		}
	}
}
