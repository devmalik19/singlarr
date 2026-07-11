package devmalik19.singlarr.repository;

import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dao.Library;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer>
{
	Optional<Library> findByPath(String path);

	List<Library> findByType(FolderType type);

	@Modifying
	@Transactional
	@Query("UPDATE Library l SET l.metadataFetched = false")
	int resetAllMetadataFlags();
}
