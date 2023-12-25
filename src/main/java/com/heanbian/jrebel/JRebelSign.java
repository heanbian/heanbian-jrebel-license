package com.heanbian.jrebel;

import java.util.Arrays;

public class JRebelSign {

	private String signature;

	public void json(String clientRandomness, String guid, boolean offline, String validFrom, String validUntil) {
		var serverRandomness = "H2ulzLlh7E0=";

		var s = "";
		if (offline) {
			var arr = Arrays.asList(clientRandomness, serverRandomness, guid, String.valueOf(offline), validFrom, validUntil);
			s = String.join(";", arr);
		} else {
			var arr = Arrays.asList(clientRandomness, serverRandomness, guid, String.valueOf(offline));
			s = String.join(";", arr);
		}

		final byte[] a = License.a(s.getBytes());
		this.signature = ByteUtils.a(a);
	}

	public String getSignature() {
		return signature;
	}

}
