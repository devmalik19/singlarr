package devmalik19.singlarr.repository;

import devmalik19.singlarr.data.dao.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

}