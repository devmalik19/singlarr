package devmalik19.singlarr.repository;

import devmalik19.singlarr.data.dao.LibraryFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryFilterRepository extends JpaRepository<LibraryFilter, String>
{
}
