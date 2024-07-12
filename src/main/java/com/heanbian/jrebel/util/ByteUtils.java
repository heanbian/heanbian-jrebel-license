package com.heanbian.jrebel.util;

import java.util.Base64;
import java.util.Random;

public final class ByteUtils {
	
	private ByteUtils() {}

	private static final Random a;

	static {
		a = new Random();
	}

	public static String a(final byte[] binaryData) {
		if (binaryData == null) {
			return null;
		}
		return Base64.getEncoder().encodeToString(binaryData);
	}

	public static byte[] a(final String s) {
		if (s == null) {
			return null;
		}
		return Base64.getDecoder().decode(s.getBytes());
	}

	public static byte[] a(final int n) {
		final byte[] arr = new byte[n];
		ByteUtils.a.nextBytes(arr);
		return arr;
	}

}
