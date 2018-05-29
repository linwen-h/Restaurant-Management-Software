package util;

/**
 * A exception made when an invalid ingredient is called.
 */
public class InvalidIngredientException extends RuntimeException {
	/**
	 * Prints a message when the exception is thrown.
	 *
	 * @param message the message to be thrown.
	 */
	public InvalidIngredientException(String message) {
		super(message);
	}
}
