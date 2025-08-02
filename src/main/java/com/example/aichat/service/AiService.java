package com.example.aichat.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import com.example.aichat.entity.ChattingTest;
import com.example.aichat.repository.ChattingTestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PromptService promptService = new PromptService();
    private final ChattingTestRepository chattingTestRepository;
    
    
    @Value("${clova.api.url}")
    private String apiUrl;
    
    @Value("${clova.api.endpoint.dash}")
    private String dash;
    
    @Value("${clova.api.endpoint.hcx007}")
    private String hcx007;
    
    @Value("${clova.api.endpoint.hcx005}")
    private String hcx005;

    @Value("${clova.api.key}")
    private String apiKey;

//    @Value("${clova.model.prompt.java}")
//    private String javaPrompt;
//    
//    @Value("${clova.model.prompt.python}")
//    private String pythonPrompt;

    @Value("${clova.max-tokens}")
    private int maxTokens;

    @Value("${clova.temperature}")
    private double temperature;

    @Value("${clova.top-p}")
    private double topP;

//    public ResponseEntity<?> askAsJavaInstructor(String userPrompt, String type) {
//    	    	
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        //headers.set("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
//        //headers.set("X-NCP-APIGW-API-KEY", apiKey);
//        headers.set("Authorization", "Bearer "+apiKey);
//        
//        
//        Map<String, Object> body = new HashMap<>();
//        
//        if(type.equals("JAVA"))
//        {
//        	body.put("messages", new Object[] {
//            		Map.of("role", "system", "content", javaPrompt),
//                    Map.of("role", "user", "content", userPrompt)
//            });
//        }
//        
//        if(type.equals("PYTHON"))
//        {
//        	body.put("messages", new Object[] {
//            		Map.of("role", "system", "content", pythonPrompt),
//                    Map.of("role", "user", "content", userPrompt)
//            });
//        }
//              
//        body.put("topP", topP);
//        body.put("temperature", temperature);
//        body.put("maxCompletionTokens", maxTokens);
//        body.put("includeAiFilters", true);
//        
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
//            JsonNode root = objectMapper.readTree(response.getBody());
//            String answer = root.path("result").path("message").path("thinkingContent").asText();
//            ChatResponseDto ChatResponse = new ChatResponseDto(answer);    
//  
//            return ResponseEntity.ok(ChatResponse);
//        } catch (Exception e) {
//        	
//            log.info("answer : "+e.getMessage());
//
//        	
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","AI 응답 처리 중 오류 발생: " + e.getMessage()));
//        }
//    }
    
    public ResponseEntity<?> chatAi(Map<String,String> requestChat) throws IOException {
    	
    	log.info("=========================[ Chat Ai ]=========================");
    	
    	String member = "문진호";
    	String soulmate = "소울이";
    	String message = requestChat.get("message");
    	
    	String systemPrompt = promptService.getSystemPrompt("dash");
    	
    	 Map<String, String> replacements = Map.of(
    		        "soulmate", soulmate,
    		        "member", member
    		    );
    	
    	String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
    	
    	
    	// 1. 사용자의 질문 insert
    	ChattingTest chattingTest = ChattingTest.builder()
    								.chatId(1L)
    								.memberId(1L)
    								.message(message)
    								.chatType("M")
    								.answerType("N")
    								.build();
    								
    	chattingTestRepository.saveAndFlush(chattingTest);
    	
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         headers.set("Authorization", "Bearer "+apiKey);
         
         
         Map<String, Object> body = new HashMap<>();
         body.put("messages", new Object[] {
         		Map.of("role", "system", "content", fullPrompt),
                Map.of("role", "user", "content", message)
         });

         
         body.put("topP", topP);
         body.put("temperature", temperature);
         body.put("maxTokens", maxTokens);
         body.put("includeAiFilters", true);
         
         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

         try {
             ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+dash, request, String.class);
             JsonNode root = objectMapper.readTree(response.getBody());
             String answer = root.path("result").path("message").path("content").asText();
             
             //2. ai의 응답 insert
             ChattingTest chattingTestAi = ChattingTest.builder()
						.chatId(1L)
						.memberId(1L)
						.message(answer)
						.chatType("A")
						.answerType("N")
						.build();
						
             chattingTestRepository.saveAndFlush(chattingTestAi);
             
             
             //3. 정보량 충족 판단
             String checkFinish = checkFinish(1L);
             String reportYn = "N";
             
             if(checkFinish.equals("Yes"))
             {
            	 reportYn = "Y";
             }
                          
             
             Map<String, String> result = new HashMap<String, String>();
                          
             result.put("answer", answer);
             result.put("reportYn", reportYn);
                      
             return ResponseEntity.ok(result);
         } catch (Exception e) {
         	
             log.info("answer : "+e.getMessage());        	
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","AI 응답 처리 중 오류 발생: " + e.getMessage()));
         }
    }
    
//    public ResponseEntity<?> makeReport(Map<String,String> request) {
//       	
//    }
    
    
    private String checkFinish(Long memberId) throws IOException
    {
    	
    	log.info("=========================[ Check Finish ]=========================");
    	
    	List<ChattingTest> chattingList = chattingTestRepository.findByMemberId(memberId);
    	String userprompt = "";
    	
    	userprompt+="--- 대화 시작 ---\n";
    	
    	for(ChattingTest chat : chattingList)
    	{
    		
    		if(chat.getChatType().equals("N"))
    			userprompt+="User : "+chat.getMessage()+"\n";
    		if(chat.getChatType().equals("A"))
    			userprompt+="Assistant : "+chat.getMessage()+"\n";   		   		
    	}  	
    	
    	userprompt+="--- 대화 끝 ---\n";
    	
    	userprompt+="이 대화를 바탕으로 보고서를 작성하기에 필요한 정보가 충분한가요?\n"
    			+ "[답변은 answer로만 해주세요]\n";
    	
    	
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+apiKey);
        
        String systemPrompt = promptService.getSystemPrompt("hcx005");
        
        
        log.info("systemPrompt : "+systemPrompt);
        
        Map<String, String> replacements = Map.of(
		        "member", "문진호"
		    );
	
	    String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
	    
	    
        Map<String, Object> body = new HashMap<>();
        body.put("messages", new Object[] {
        		Map.of("role", "system", "content", fullPrompt),
                Map.of("role", "user", "content", userprompt)
        });

        body.put("topP", topP);
        body.put("temperature", temperature);
        body.put("maxTokens", maxTokens);
        //body.put("maxCompletionTokens", maxTokens);
        body.put("includeAiFilters", true);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+hcx005, request, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        
        log.info("answer1 : "+root.toString());
        
        String answer = root.path("result").path("message").path("content").asText();
        //String answer = root.path("result").path("message").path("thinkingContent").asText();
        
        
        log.info("answer : "+answer);
        
        return answer;
    	
    }
    
   
}
