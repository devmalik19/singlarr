package devmalik19.singlarr.data.dao;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Settings
{
	@Id
	private String key;
	private String value;
}
