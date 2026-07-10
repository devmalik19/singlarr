package devmalik19.singlarr.constants;

import org.springframework.util.StringUtils;

public enum FileTypes
{
	MP3,
	OPUS,
	FLAC;

	public static boolean isMatch(String extension)
	{
		if (!StringUtils.hasText(extension))
			return false;

		try
		{
			FileTypes.valueOf(extension.toUpperCase());
			return true;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}
}
