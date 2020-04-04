package ph.devcon.rapidpass.utilities;

public class StringFormatter {

	public static String normalize(String format) {
		final String NON_ALPHANUMERIC_REGEX = "[^A-Za-z0-9]+";
		return format.replaceAll(NON_ALPHANUMERIC_REGEX, "").toUpperCase();
	}
}
