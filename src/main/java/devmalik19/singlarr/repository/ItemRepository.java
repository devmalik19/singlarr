package devmalik19.singlarr.repository;

import devmalik19.singlarr.data.dao.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String>
{
	Optional<Item> findByPath(String path);
}
