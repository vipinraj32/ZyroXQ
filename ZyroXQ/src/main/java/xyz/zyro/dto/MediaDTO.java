package xyz.zyro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaDTO {

	private String id;
    private String caption;
    private String media_type;
    private String media_url;
    private String permalink;
    private String thumbnail_url;
    private String timestamp;
}
