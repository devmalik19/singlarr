package devmalik19.singlarr.repository;

import devmalik19.singlarr.data.dao.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long>
{
}
