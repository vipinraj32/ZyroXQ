package xyz.zyro.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowCampaignDTO {
	private Long campaignId;
    private String companyName;
    private Integer payPerInfluncer;
    private Integer budget;
    private String requiment;
    private LocalDate startDate;
    private LocalDate endDate;

    private String fileName;
    private String fileType;
}
