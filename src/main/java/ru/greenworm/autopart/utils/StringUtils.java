package ru.greenworm.autopart.utils;

import java.util.Arrays;

public final class StringUtils {

	private StringUtils() {

	}

	public static String getDeclensionString(final long number, String string1, String string24, String string50) {
		long digit = (number < 0 ? (number - number * 2) : number) % 100;
		digit = digit > 20 ? digit % 10 : digit;
		return Long.toString(number) + " " + (digit == 1 ? string1 : (digit > 4 || digit < 1 ? string50 : string24));
	}

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static boolean isAnyEmpty(String... strings) {
		return Arrays.stream(strings).anyMatch(StringUtils::isEmpty);
	}

	public static boolean hasLength(String string) {
		return string != null && string.length() > 0;
	}
	
	public static float parseFloat(String string, float defaultValue) {
		try {
			return Float.parseFloat(string);
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
