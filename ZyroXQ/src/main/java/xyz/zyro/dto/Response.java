package xyz.zyro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
	private String token;
	private String email;
	private String role;
	private String username;
	private Boolean profileStatus;
}
