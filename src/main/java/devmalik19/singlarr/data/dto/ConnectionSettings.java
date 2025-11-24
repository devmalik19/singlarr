package devmalik19.singlarr.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import devmalik19.singlarr.helper.JsonDecrypt;
import devmalik19.singlarr.helper.JsonEncrypt;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionSettings
{
    private String name;
    private String url;
    @JsonSerialize(using = JsonEncrypt.class)
    @JsonDeserialize(using = JsonDecrypt.class)
    private String apiKey;
    @JsonSerialize(using = JsonEncrypt.class)
    @JsonDeserialize(using = JsonDecrypt.class)
    private String username;
    @JsonSerialize(using = JsonEncrypt.class)
    @JsonDeserialize(using = JsonDecrypt.class)
    private String password;
	private String category;
}
