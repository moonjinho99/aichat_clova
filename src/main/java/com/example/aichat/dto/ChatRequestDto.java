package com.example.aichat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
	private String prompt;
	private String type;
}
