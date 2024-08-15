package com.heanbian.jrebel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MainServer extends AbstractHandler {

	private static Map<String, String> argumentsOf(String[] args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("Error in argument's length ");
		}

		var params = new HashMap<String, String>();

		for (int i = 0, len = args.length; i < len;) {
			var argName = args[i++];

			if (argName.charAt(0) == '-') {
				if (argName.length() < 2) {
					throw new IllegalArgumentException("Error at argument " + argName);
				}

				argName = argName.substring(1);
			}

			params.put(argName, args[i++]);
		}

		return params;
	}

	public static void main(String[] args) throws Exception {
		var arguments = argumentsOf(args);
		var port = arguments.get("p");

		if (port == null || !port.matches("\\d+")) {
			port = "7881";
		}

		var server = new Server(Integer.parseInt(port));
		server.setHandler(new MainServer());
		server.start();

		System.out.println("Successfully started !");
		server.join();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		switch (target) {
		case "/" -> indexHandler(target, baseRequest, request, response);
		case "/jrebel/leases" -> jrebelLeasesHandler(target, baseRequest, request, response);
		case "/jrebel/leases/1" -> jrebelLeases1Handler(target, baseRequest, request, response);
		case "/agent/leases" -> jrebelLeasesHandler(target, baseRequest, request, response);
		case "/agent/leases/1" -> jrebelLeases1Handler(target, baseRequest, request, response);
		case "/jrebel/validate-connection" -> jrebelValidateHandler(target, baseRequest, request, response);
		case "/rpc/ping.action" -> pingHandler(target, baseRequest, request, response);
		case "/rpc/obtainTicket.action" -> obtainTicketHandler(target, baseRequest, request, response);
		case "/rpc/releaseTicket.action" -> releaseTicketHandler(target, baseRequest, request, response);
		default -> response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	private void jrebelValidateHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var json = """
				   {
				        "serverVersion": "3.2.4",
				        "serverProtocolVersion": "1.1",
				        "serverGuid": "a1b4aea8-b031-4302-b602-670a990272cb",
				        "groupType": "managed",
				        "statusCode": "SUCCESS",
				        "company": "Administrator",
				        "canGetLease": true,
				        "licenseType": 1,
				        "evaluationLicense": false,
				        "seatPoolType": "standalone"
				    }
				""";
		var obj = new JSONObject(json);
		response.getWriter().print(obj.toString());
	}

	private void jrebelLeases1Handler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var json = """
				    {
				        "serverVersion": "3.2.4",
				        "serverProtocolVersion": "1.1",
				        "serverGuid": "a1b4aea8-b031-4302-b602-670a990272cb",
				        "groupType": "managed",
				        "statusCode": "SUCCESS",
				        "msg": null,
				        "statusMessage": null
				    }
				""";
		var obj = new JSONObject(json);

		var username = request.getParameter("username");
		if (username != null) {
			obj.put("company", username);
		}
		response.getWriter().print(obj.toString());

	}

	private void jrebelLeasesHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var clientRandomness = request.getParameter("randomness");
		var username = request.getParameter("username");
		var guid = request.getParameter("guid");
		var offline = Boolean.parseBoolean(request.getParameter("offline"));
		var validFrom = "null";
		var validUntil = "null";

		if (offline) {
			var clientTime = request.getParameter("clientTime");
			long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
			validFrom = clientTime;
			validUntil = String.valueOf(clinetTimeUntil);
		}

		var json = """
				{
				    "serverVersion": "3.2.4",
				    "serverProtocolVersion": "1.1",
				    "serverGuid": "a1b4aea8-b031-4302-b602-670a990272cb",
				    "groupType": "managed",
				    "id": 1,
				    "licenseType": 1,
				    "evaluationLicense": false,
				    "signature": "OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY=",
				    "serverRandomness": "H2ulzLlh7E0=",
				    "seatPoolType": "standalone",
				    "statusCode": "SUCCESS",
				    "offline": %s,
				    "validFrom": %s,
				    "validUntil": %s,
				    "company": "Administrator",
				    "orderId": "",
				    "zeroIds": [],
				    "licenseValidFrom": 1490544001000,
				    "licenseValidUntil": 1691839999000
				}
				"""
				.formatted(offline, validFrom, validUntil);

		var obj = new JSONObject(json);
		if (clientRandomness == null || username == null || guid == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			var s = new JRebelSign(clientRandomness, guid, offline, validFrom, validUntil);
			obj.put("signature", s.getSignature());
			obj.put("company", username);
			response.getWriter().print(obj.toString());
		}
	}

	private void releaseTicketHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var salt = request.getParameter("salt");
		if (salt == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			var xmlContent = """
					<ReleaseTicketResponse>
					    <message></message>
					    <responseCode>OK</responseCode>
					    <salt>%s</salt>
					</ReleaseTicketResponse>
					""".formatted(salt);

			var xmlSignature = RsaSign.sign(xmlContent);
			var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
			response.getWriter().print(body);
		}
	}

	private void obtainTicketHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var salt = request.getParameter("salt");
		var username = request.getParameter("userName");
		var prolongationPeriod = "607875500";
		if (salt == null || username == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			var xmlContent = """
					    <ObtainTicketResponse>
					        <message></message>
					        <prolongationPeriod>%s</prolongationPeriod>
					        <responseCode>OK</responseCode>
					        <salt>%s</salt>
					        <ticketId>1</ticketId>
					        <ticketProperties>license=%s\tlicenseType=0\t</ticketProperties>
					    </ObtainTicketResponse>
					""".formatted(prolongationPeriod, salt, username);
			var xmlSignature = RsaSign.sign(xmlContent);
			var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
			response.getWriter().print(body);
		}
	}

	private void pingHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		var salt = request.getParameter("salt");
		if (salt == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			var xmlContent = """
					    <PingResponse>
					        <message></message>
					        <responseCode>OK</responseCode>
					        <salt>%s</salt>
					    </PingResponse>
					""".formatted(salt);
			var xmlSignature = RsaSign.sign(xmlContent);
			var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
			response.getWriter().print(body);
		}
	}

	private void indexHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		String scheme = request.getScheme();
		int port = request.getServerPort();
		String domain = request.getServerName();

		var sb = new StringBuilder();
		sb.append(scheme).append("://").append(domain);
		if (port != 80 || port != 443) {
			sb.append(':').append(port);
		}

		var licenseServer = sb.toString();
		var guid = UUID.randomUUID().toString();
		var licenseUrl = licenseServer + "/" + guid;

		var html = """
				<!DOCTYPE html>
				<html lang="zh-CN">
				<head>
				<meta charset="UTF-8">
				<title>JRebel & JetBrains License Server</title>
				<style>
				.content {
					width: 750px;
					margin: 100px auto;
				}

				html, body, div, p, h3 {
					color: #000000;
					background-color: #f5f5f5;
					font-size: 14px;
					line-height: 1.5;
					margin: 0;
					padding: 0;
					font-family: system-ui, -apple-system, Segoe UI, Roboto, Ubuntu,
						Cantarell, Noto Sans, sans-serif;
				}

				.mt5 {
					margin-top: 5px;
				}

				.mt20 {
					margin-top: 20px;
				}

				.mt30 {
					margin-top: 30px;
				}

				a {
					color: #066ef4;
				}
				</style>
				</head>
				<body>
				<div class="content">
					<h3>JRebel & JetBrains License Server</h3>

					<p class="mt20">JetBrains 激活地址是：%s</p>
					<p class="mt5">JRebel 激活地址是：%s <a href="javascript:copyText();">复制</a> <a href="/">刷新</a></p>

					<p class="mt30">GitHub 地址：<a href="https://github.com/heanbian/heanbian-jrebel-license">https://github.com/heanbian/heanbian-jrebel-license</a></p>
				</div>
				<script>
					var _text = "%s";
					function copyText() {
						if (navigator.clipboard) {
							navigator.clipboard.writeText(_text).then(() => {
								alert("已复制到剪贴板");
							}).catch((error) => {
								alert("复制文本失败：" + error);
							})
						} else {
							alert("您的浏览器不支持复制");
						}
					}
				</script>
				</body>
				</html>
				"""
				.formatted(licenseServer, licenseUrl, licenseUrl);

		response.getWriter().println(html);
	}
}