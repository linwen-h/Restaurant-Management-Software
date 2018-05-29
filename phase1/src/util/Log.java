package util;

/**
 * A logger that prints to console with reference to Logger.
 */
public class Log {
	/**
	 * Prints to console with reference to Logger when an object has an id.
	 *
	 * @param logger A logger to log messages for a specific system or application component.
	 * @param id     an id.
	 * @param str    the message to log.
	 */
	public static void logID(Object logger, int id, String str) {
		System.out.printf("LOG [%s][%d]:\t%s%s", logger.toString(), id, str, System.lineSeparator());
		System.out.flush();
	}

	/**
	 * Prints to console with reference to Logger when an object does not have an id.
	 *
	 * @param logger A logger to log messages for a specific system or application component.
	 * @param str    the message to log.
	 */
	public static void log(Object logger, String str) {
		System.out.printf("LOG [%s]:\t%s%s", logger.toString(), str, System.lineSeparator());
		System.out.flush();
	}
}
