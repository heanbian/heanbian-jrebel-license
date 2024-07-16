package com.heanbian.jrebel.web;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/")
	public String index(Model model) {
		// 自定义
		var licenseServer = "https://jrebel.heanbian.com";

		var guid = UUID.randomUUID().toString();
		var licenseUrl = licenseServer + "/" + guid;

		model.addAttribute("license_server", licenseServer);
		model.addAttribute("license_url", licenseUrl);
		return "index";
	}

}
