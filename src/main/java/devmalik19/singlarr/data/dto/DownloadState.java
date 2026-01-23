package devmalik19.singlarr.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadState
{
	private String identifier;
	private String service;
	private String downloadPath;
}
