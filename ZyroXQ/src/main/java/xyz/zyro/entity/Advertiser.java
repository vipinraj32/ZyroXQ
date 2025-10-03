package xyz.zyro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	@NotBlank(message = "Company name must be required")
    private String companyName;
//	@NotBlank(message = "Connect your wallet")
	private String walletAddres;
	@Lob
	private byte[] imageData;
	private String imageName;
	private String imageType;
	
	@OneToOne(mappedBy = "advertiser")
	private User user; 	
	
	
}
