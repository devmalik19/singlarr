package devmalik19.singlarr.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult
{
    private String guid;
    private String indexer;
    private String title;
    private String downloadUrl;
    private int seeders;
    private int leechers;
    private String protocol;
}
