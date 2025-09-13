package xyz.zyro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class Advertiser {
	@Id
	@NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
	private String email;
	 @NotBlank(message = "Password is required")
	 @Size(min = 8,max = 200, message = "Password must be at least 8 characters long. ")
	private String password;
	 @NotBlank(message = "name must be required")
	private String name;
	@NotBlank(message = "Company name must be required")
	private String companyName;
	@NotBlank(message = "Connect your wallet")
	private String walletAddres;
	@NotEmpty(message = "stake Amount is not $0.0")
	@Size(min = 8,max = 200, message = "Password must be at least 8 characters long. ")
    private Double stakeAmount;
	@NotBlank(message = "contact number must be required")
    private String mobile;
	
	
	
}
