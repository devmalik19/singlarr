package devmalik19.singlarr.repository;

import devmalik19.singlarr.data.dao.Blocklist;
import devmalik19.singlarr.data.dao.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlocklistRepository extends JpaRepository<Blocklist, Integer>
{
	List<Blocklist> findBySearch(Search search);

	void deleteBySearch(Search search);
}
