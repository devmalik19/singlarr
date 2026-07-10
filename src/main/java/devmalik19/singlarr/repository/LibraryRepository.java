package devmalik19.singlarr.repository;

import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dao.Library;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Integer>
{
	Optional<Library> findByPath(String path);

	List<Library> findByType(FolderType type);
}
