package com.example.aichat.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.aichat.dto.ChatRequestDto;
import com.example.aichat.service.AiService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;
	
	@GetMapping("/chat")
	public String chatPage()
	{
		return "chat";
	}
	
//	@PostMapping("/chat")
//	public ResponseEntity<?> chat(@RequestBody ChatRequestDto request) {			
//		return aiService.askAsJavaInstructor(request.getPrompt(), request.getType());
//	}
	
	
	@PostMapping("/chat")
	public ResponseEntity<?> chatAi(@RequestBody Map<String,String> request) throws IOException {		
		return aiService.chatAi(request);
	}
	
//	@PostMapping("/report")
//	public ResponseEntity<?> makeReport(@RequestBody Map<String,String> request) {		
//		return aiService.makeReport(request);
//	}

}
