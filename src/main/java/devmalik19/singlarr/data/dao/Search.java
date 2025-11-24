package devmalik19.singlarr.data.dao;


import devmalik19.singlarr.constants.SearchStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Search
{
	@Id
	private String title;
	private String artist;
	private String album;
	private String year;
	private SearchStatus status;
}
