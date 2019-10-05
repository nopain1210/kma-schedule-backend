package codes.nopain.nopain.app.database.repository;

import codes.nopain.nopain.app.database.document.UserSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSchedulesRepository extends MongoRepository<UserSchedule, String> {
    Optional<UserSchedule> findByEmail(String email);
}
