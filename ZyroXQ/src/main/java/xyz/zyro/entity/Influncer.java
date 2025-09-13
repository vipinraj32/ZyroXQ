package xyz.zyro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Influncer {

	@Id
	@NotBlank(message = "username must be required")
	private String username;
	private String email;
	private String walletAddress;
	private String name;
	
}
