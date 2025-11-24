package devmalik19.singlarr.data.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "indexes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Index
{
    @Id
    private int id;
    private String name;
	private int[] tags;
	private boolean enable;
}