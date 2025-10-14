package xyz.zyro.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@lombok.Data
public class InstagramMediaResponseDTO {

  private List<MediaDTO> Data;
}
