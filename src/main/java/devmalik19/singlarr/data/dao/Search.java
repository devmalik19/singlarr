package devmalik19.singlarr.data.dao;


import devmalik19.singlarr.constants.SearchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Search
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String artist;
	private String album;
	private String year;
	private SearchStatus status;

	@ManyToOne
	@JoinColumn(name = "library")
	private Library library;
}
