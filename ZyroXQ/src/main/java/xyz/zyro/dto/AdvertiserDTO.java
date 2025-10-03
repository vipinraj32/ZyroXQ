package xyz.zyro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertiserDTO {

	private String companyName;
	private String walletAddres;
    private byte[] profileImage;
    private String imageName;
}
