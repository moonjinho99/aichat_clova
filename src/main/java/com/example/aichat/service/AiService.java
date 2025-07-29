package com.example.aichat.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.aichat.dto.ChatResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.model.prompt.java}")
    private String javaPrompt;
    
    @Value("${clova.model.prompt.python}")
    private String pythonPrompt;

    @Value("${clova.model.max-tokens}")
    private int maxTokens;

    @Value("${clova.model.temperature}")
    private double temperature;

    @Value("${clova.model.top-p}")
    private double topP;

    public ResponseEntity<?> askAsJavaInstructor(String userPrompt, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
        //headers.set("X-NCP-APIGW-API-KEY", apiKey);
        headers.set("Authorization", "Bearer "+apiKey);
        
        
        Map<String, Object> body = new HashMap<>();
        
        if(type.equals("JAVA"))
        {
        	body.put("messages", new Object[] {
            		Map.of("role", "system", "content", javaPrompt),
                    Map.of("role", "user", "content", userPrompt)
            });
        }
        
        if(type.equals("PYTHON"))
        {
        	body.put("messages", new Object[] {
            		Map.of("role", "system", "content", pythonPrompt),
                    Map.of("role", "user", "content", userPrompt)
            });
        }
              
        body.put("topP", topP);
        body.put("temperature", temperature);
        body.put("maxTokens", maxTokens);
        body.put("includeAiFilters", true);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            String answer = root.path("result").path("message").path("content").asText();
            ChatResponseDto ChatResponse = new ChatResponseDto(answer);    
            
            return ResponseEntity.ok(ChatResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","AI 응답 처리 중 오류 발생: " + e.getMessage()));
        }
    }
}
