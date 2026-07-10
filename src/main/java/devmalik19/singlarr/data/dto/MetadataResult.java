package devmalik19.singlarr.data.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class MetadataResult
{
	private String title;
	private String artists;
	private String albums;
	private String year;
	private Integer library;

	@Override
	public String toString()
	{
		try
		{
			return new ObjectMapper().writeValueAsString(this);
		}
		catch (Exception e)
		{
			return "{}";
		}
	}
}




