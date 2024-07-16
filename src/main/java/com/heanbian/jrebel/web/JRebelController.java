package com.heanbian.jrebel.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heanbian.jrebel.util.JRebelSign;

import reactor.core.publisher.Mono;

@RestController
public class JRebelController {

	@PostMapping({ "/jrebel/leases", "/agent/leases" })
	public Mono<Map<String, Object>> leases(String randomness, String username, String guid, boolean offline, String clientTime) {
		var validFrom = "null";
		var validUntil = "null";

		Map<String, Object> map = new HashMap<>();
		map.put("serverVersion", "3.2.4");
		map.put("serverProtocolVersion", "1.1");
		map.put("serverGuid", guid);
		map.put("groupType", "managed");
		map.put("id", 1);
		map.put("licenseType", 1);
		map.put("evaluationLicense", false);
		map.put("serverRandomness", "H2ulzLlh7E0=");
		map.put("seatPoolType", "standalone");
		map.put("statusCode", "SUCCESS");
		map.put("offline", offline);
		map.put("company", username);
		map.put("orderId", "");
		map.put("zeroIds", List.of());
		map.put("licenseValidFrom", 1490544001000L);
		map.put("licenseValidUntil", 1691839999000L);

		if (offline) {
			validFrom = clientTime;
			validUntil = String.valueOf(Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000);

			map.put("validFrom", clientTime);
			map.put("validUntil", validUntil);
		} else {
			map.put("validFrom", validFrom);
			map.put("validUntil", validUntil);
		}

		var s = new JRebelSign(randomness, guid, offline, clientTime, validUntil);
		map.put("signature", s.getSignature());
		return Mono.just(map);
	}

	@PostMapping({ "/jrebel/leases/1", "/agent/leases/1" })
	public Mono<Map<String, Object>> leases1(String username, String guid) {
		Map<String, Object> map = new HashMap<>();
		map.put("serverVersion", "3.2.4");
		map.put("serverProtocolVersion", "1.1");
		map.put("serverGuid", guid);
		map.put("groupType", "managed");
		map.put("statusCode", "SUCCESS");
		map.put("msg", null);
		map.put("statusMessage", null);
		map.put("company", username);
		return Mono.just(map);
	}

	@PostMapping("/jrebel/validate-connection")
	public Mono<Map<String, Object>> validateConnection(String username, String guid) {
		Map<String, Object> map = new HashMap<>();
		map.put("serverVersion", "3.2.4");
		map.put("serverProtocolVersion", "1.1");
		map.put("serverGuid", guid);
		map.put("groupType", "managed");
		map.put("statusCode", "SUCCESS");
		map.put("company", username);
		map.put("canGetLease", true);
		map.put("licenseType", 1);
		map.put("evaluationLicense", false);
		map.put("seatPoolType", "standalone");
		return Mono.just(map);
	}

}
