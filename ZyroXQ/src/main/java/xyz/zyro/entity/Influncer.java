package xyz.zyro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	@NotBlank(message = "email must be required")
	private String email;
	@NotBlank(message = "walletAddress must be required")
	private String walletAddress;
	@NotBlank(message = "name must be required")
	private String name;
	@OneToOne(mappedBy = "influncer")
	private User user;
	
}
