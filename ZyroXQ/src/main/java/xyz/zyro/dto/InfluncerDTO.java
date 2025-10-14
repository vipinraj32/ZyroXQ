package xyz.zyro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfluncerDTO {
	
	private String email;
	private String walletAddress;
	private String name;
}
