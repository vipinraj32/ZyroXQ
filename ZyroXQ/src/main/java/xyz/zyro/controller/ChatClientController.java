package xyz.zyro.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatClientController {
	private ChatClient chatClient;
	
	public ChatClientController(ChatClient.Builder chatClient) {
		this.chatClient=chatClient.build();
	}
	
	@GetMapping("/openai")
	public ResponseEntity<String> ollamAi(String prompt){
		var resultResponse=chatClient.prompt(prompt).call().content();
		return ResponseEntity.ok(resultResponse);
	}
	
	

}
