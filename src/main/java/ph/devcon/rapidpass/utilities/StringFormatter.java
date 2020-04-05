package ph.devcon.rapidpass.utilities;

public class StringFormatter {

	/**
	 * Normalizes a piece of string to only contain alphanumeric values (a-z, A-Z, and 0-9).
	 * @param input The input string to process
	 * @return The input string, stripped of all characters except alphanumeric values.
	 */
	public static String normalizeAlphanumeric(String input) {
		if (input == null) {
			throw new IllegalArgumentException("Failed to normalize string because it was null.");
		}
		final String NON_ALPHANUMERIC_REGEX = "[^A-Za-z0-9]+";
		return input.replaceAll(NON_ALPHANUMERIC_REGEX, "").toUpperCase();
	}
}
