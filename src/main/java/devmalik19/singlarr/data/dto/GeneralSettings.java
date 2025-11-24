package devmalik19.singlarr.data.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class GeneralSettings
{
	private String scheduleTime;
	private HashMap<String, Boolean> services;
}
