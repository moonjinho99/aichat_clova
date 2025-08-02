package com.example.aichat.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

@Service
public class PromptService {

	public String getSystemPrompt(String model) throws IOException {
        String path = "prompt/%s.yml".formatted(model.toLowerCase());
        Yaml yaml = new Yaml();
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            Map<String, String> data = yaml.load(input);
            return data.get("system");
        }
    }

    public String buildFinalPrompt(String systemPrompt, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            systemPrompt = systemPrompt.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return systemPrompt;
    }
}
