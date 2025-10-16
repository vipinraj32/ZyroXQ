package xyz.zyro.config;

import org.springframework.ai.chat.client.ChatClient;

public class ChatClientConfig {
	
	private ChatClient ollamaChatClient;
	public ChatClientConfig(ChatClient.Builder ollamaChatClient) {
		this.ollamaChatClient=ollamaChatClient.build();
	}
}
