package xyz.zyro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserDTO {

	private String name;
	private String companyName;
	private String walletAddres;
    private Double stakeAmount;
    private String mobile;
}
