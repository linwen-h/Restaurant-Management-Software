package util;

import visual.Login;

/**
 * A logger that prints to console with reference to Logger.
 */
public class Log {

	/**
	 * Prints to console with reference to Logger when an object has an id.
	 *
	 * @param log A logger to log messages for a specific system or application component.
	 * @param id  an id.
	 * @param str the message to log.
	 */
	public static void logID(Object log, int id, String str) {
		Login.logger.info(String.format("LOG [%s][%s]:\t%s%s", log.toString(), id, str, System.lineSeparator()));
	}
	//TODO: prevent file locking....
	//TODO: Why are logins not being logged?
	//TODO: change way fh and logger are called...

	/**
	 * Prints to console with reference to Logger when an object does not have an id.
	 *
	 * @param log A logger to log messages for a specific system or application component.
	 * @param str the message to log.
	 */
	public static void log(Object log, String str) {
		Login.logger.info(String.format("LOG [%s]:\t %s%s", log.toString(), str, System.lineSeparator()));
	}

	/*
	 * Prints to console with reference to Logger when an object does not have an id.
	 *
	 * @param log A logger to log messages for a specific system or application component.
	 * @param str the message to log.
	 */
	public static void warn(Object log, String str) {
		Login.logger.warning(String.format("LOG [%s]:\t %s%s", log.toString(), str, System.lineSeparator()));
	}
}
