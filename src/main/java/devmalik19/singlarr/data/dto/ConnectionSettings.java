package devmalik19.singlarr.data.dto;

import lombok.Data;

@Data
public class ConnectionSettings
{
    private String name;
    private String url;
    private String apiKey;
	private String[] tags;
}
