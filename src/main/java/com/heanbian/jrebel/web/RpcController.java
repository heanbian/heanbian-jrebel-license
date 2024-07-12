package com.heanbian.jrebel.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.heanbian.jrebel.util.RsaSign;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/rpc")
public class RpcController {

	@PostMapping(value = "/ping.action", produces = MediaType.TEXT_HTML_VALUE)
	public Mono<String> ping(String salt) {
		var xmlContent = """
				   <PingResponse>
				       <message></message>
				       <responseCode>OK</responseCode>
				       <salt>%s</salt>
				   </PingResponse>
				""".formatted(salt);

		var xmlSignature = RsaSign.sign(xmlContent);
		var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
		return Mono.just(body);
	}

	@PostMapping(value = "obtainTicket.action", produces = MediaType.TEXT_HTML_VALUE)
	public Mono<String> obtainTicket(String salt, String userName) {
		var xmlContent = """
				   <ObtainTicketResponse>
				       <message></message>
				       <prolongationPeriod>607875500</prolongationPeriod>
				       <responseCode>OK</responseCode>
				       <salt>%s</salt>
				       <ticketId>1</ticketId>
				       <ticketProperties>license=%s\tlicenseType=0\t</ticketProperties>
				   </ObtainTicketResponse>
				""".formatted(salt, userName);

		var xmlSignature = RsaSign.sign(xmlContent);
		var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
		return Mono.just(body);
	}

	@PostMapping(value = "releaseTicket.action", produces = MediaType.TEXT_HTML_VALUE)
	public Mono<String> releaseTicket(String salt) {
		var xmlContent = """
				<ReleaseTicketResponse>
				    <message></message>
				    <responseCode>OK</responseCode>
				    <salt>%s</salt>
				</ReleaseTicketResponse>
				""".formatted(salt);

		var xmlSignature = RsaSign.sign(xmlContent);
		var body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
		return Mono.just(body);
	}

}
