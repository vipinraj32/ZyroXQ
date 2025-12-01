package xyz.zyro.dto;

import java.time.LocalDate;

import javax.validation.constraints.Future;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampaignCreateDTO {

	@NotBlank(message = "Company name must be required")
	private String companyName;
	@NotBlank(message = "Atleast! Enter a one requirment")
	private String requirment;
	@Future(message = "Appointment date must be in the future")
	private LocalDate date;
	private String campaignType;
	@NotNull(message = "Campaign Number is required")
	private Integer totalCampaign;
	@NotNull(message = "view is required")
	@Min(value = 18, message = "View must be at least 1000")
	private Integer view;
	private Integer amount;
	
}
