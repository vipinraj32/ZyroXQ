package xyz.zyro.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryCampaignDTO {
	
	@NotNull(message = "Company_id Must Be Created")
	private Integer campaignId;
	@NotBlank(message = "Company name must be required")
	private String companyName;
	@NotNull(message = "Influncer Pay Amount Must be Grater then 0")
	private Integer payPerInfluncer;
	@NotNull(message = "budget Not 0")
	private Integer budget;
	private String requiment;
	private LocalDate startDate;
	private LocalDate endDate;
	
}
