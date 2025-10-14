package xyz.zyro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileDetailsDTO {
	
	private String id;
	private String username;
	private String followers_count;
	private String account_type;
	private Integer media_count;
	private String name;

}
