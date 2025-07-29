package com.example.aichat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.aichat.dto.ChatRequestDto;
import com.example.aichat.service.AiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;
	
	@GetMapping("/chat")
	public String chatPage()
	{
		return "chat";
	}
	
	@PostMapping("/chat")
	public String chat(@ModelAttribute ChatRequestDto request, Model model) {
		String result = aiService.askAsJavaInstructor(request.getPrompt());
		model.addAttribute("response",result);
		model.addAttribute("prompt",request.getPrompt());
		
		return "chat";
	}

}
