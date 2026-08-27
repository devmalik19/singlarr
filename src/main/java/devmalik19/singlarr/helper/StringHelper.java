package devmalik19.singlarr.helper;

public class StringHelper
{
	public static String buildQuery(String... parts)
	{
		StringBuilder sb = new StringBuilder();
		for (String part : parts)
		{
			if (part != null && !part.isBlank())
			{
				if (!sb.isEmpty()) sb.append(" ");
				sb.append(part);
			}
		}
		return sb.toString();
	}
}
