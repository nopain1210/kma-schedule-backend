package codes.nopain.nopain.app.database.repository;

import codes.nopain.nopain.app.database.document.UserSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends MongoRepository<UserSetting, String> {
    Optional<UserSetting> findByEmail(String email);
}
