package com.heanbian.jrebel.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heanbian.jrebel.util.JRebelSign;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class JRebelController {

	@PostMapping({ "/jrebel/leases", "/agent/leases" })
	public Map<String, Object> leases(HttpServletRequest request) {
		var clientRandomness = request.getParameter("randomness");
		var username = request.getParameter("username");
		var guid = request.getParameter("guid");
		var offline = Boolean.parseBoolean(request.getParameter("offline"));
		var validFrom = "null";
		var validUntil = "null";

		if (offline) {
			var clientTime = request.getParameter("clientTime");
//			long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
			long clinetTimeUntil = System.currentTimeMillis() + 180L * 24 * 60 * 60 * 1000;
			validFrom = clientTime;
			validUntil = String.valueOf(clinetTimeUntil);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("serverVersion", "3.2.4");
		map.put("serverProtocolVersion", "1.1");
		map.put("serverGuid", "a1b4aea8-b031-4302-b602-670a990272cb");
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
		map.put("validFrom", validFrom);
		map.put("validUntil", validUntil);

		var s = new JRebelSign(clientRandomness, guid, offline, validFrom, validUntil);
		map.put("signature", s.getSignature());
		return map;
	}

	@PostMapping({ "/jrebel/leases/1", "/agent/leases/1" })
	public Map<String, Object> leases1(HttpServletRequest request) {
		var username = request.getParameter("username");
		var guid = request.getParameter("guid");

		Map<String, Object> map = new HashMap<>();
		map.put("serverVersion", "3.2.4");
		map.put("serverProtocolVersion", "1.1");
		map.put("serverGuid", guid);
		map.put("groupType", "managed");
		map.put("statusCode", "SUCCESS");
		map.put("msg", null);
		map.put("statusMessage", null);
		map.put("company", username);
		return map;
	}

	@PostMapping("/jrebel/validate-connection")
	public Map<String, Object> validateConnection(HttpServletRequest request) {
		var username = request.getParameter("username");
		var guid = request.getParameter("guid");

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
		return map;
	}

}
