package com.baojie.util;


public class CheckNull {

	private CheckNull() {

	}

	public static void checkObjectNull(final Object object) {
		if (null == object) {
			throw new NullPointerException();
		}
	}

	public static byte[] checkBytesNull(final byte[] bytes) {
		if (null == bytes) {
			throw new NullPointerException("byte[] bytes must not be null.");
		}
		return bytes;
	}

	public static String checkStringEmpty(final String string) {
		checkStringNull(string);
		innerCheck(string);
		final String emptyString = string.trim();
		innerCheck(emptyString);
		return string;
	}

	private static void innerCheck(final String string) {
		if (emptyCompare(string)) {
			throw new IllegalStateException("string must not be empty. string is empty.");
		}
		if (spaceCompare(string)) {
			throw new IllegalStateException("string must not be empty. string is '_'.");
		}
	}

	private static boolean emptyCompare(final String string) {
		if ("".equals(string)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean spaceCompare(final String string) {
		if (" ".equals(string)) {
			return true;
		} else {
			return false;
		}
	}

	public static String checkStringNull(final String string) {
		if (null == string) {
			throw new NullPointerException("string must not be null.");
		}
		return string;
	}

	public static String[] checkStringArrayNull(final String[] strings) {
		if (null == strings) {
			throw new NullPointerException("String[] must not be null.");
		}
		return strings;
	}

}
