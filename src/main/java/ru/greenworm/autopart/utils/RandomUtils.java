package ru.greenworm.autopart.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

public final class RandomUtils {

	private RandomUtils() {

	}

	private final static char[] RANDOM_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'V', 'U', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private static final Random RANDOM = new Random(new Date().getTime());

	public static final int nextInt(int max) {
		return RANDOM.nextInt(max);
	}

	public static String nextString(final int length) {
		String random = "";
		for (int i = 0; i < length; i++) {
			random += RANDOM_CHARS[RANDOM.nextInt(RANDOM_CHARS.length)];
		}
		return random;
	}

	public static BigDecimal nextBigDecimal(int max) {
		return new BigDecimal(nextInt(max));
	}

	public static <E extends Enum<E>> E nextEnum(Class<E> e) {
		return e.getEnumConstants()[RandomUtils.nextInt(e.getEnumConstants().length)];
	}

	public static Date nextDate() {
		return new Date(new Date().getTime() - RANDOM.nextInt(24 * 60 * 60 * 1000));
	}

	public static <T> T arrayItem(T[] array) {
		return array[nextInt(array.length)];
	}

	public static Long nextLong(int i) {
		return new Long(nextInt(i));
	}
}
