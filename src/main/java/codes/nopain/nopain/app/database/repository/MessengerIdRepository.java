package codes.nopain.nopain.app.database.repository;

import codes.nopain.nopain.app.database.document.MessengerId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessengerIdRepository extends MongoRepository<MessengerId, String> {
    List<MessengerId> findAllBySenderId(String senderId);
    List<MessengerId> findAllByEmail(String email);
}
