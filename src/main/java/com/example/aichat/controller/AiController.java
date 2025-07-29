package com.example.aichat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<?> chat(@RequestBody ChatRequestDto request) {			
		return aiService.askAsJavaInstructor(request.getPrompt(), request.getType());
	}

}
