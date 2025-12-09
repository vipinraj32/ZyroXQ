package xyz.zyro.entity;

import java.util.Date;

import org.hibernate.annotations.GeneratorType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampaignSubmission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer submitId;
	@NotNull(message = "Campaign_Id not null.")
	private String campaignId;
	@NotBlank(message = "media_Id not null.")
	private Integer mediaId;
	@NotNull(message = "media_Url not blank!")
	private String mediaUrl;
	private String permaLink;
	private Date timedstamp;
	private String paymentStatus;
	

}
