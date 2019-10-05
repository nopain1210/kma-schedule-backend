package codes.nopain.nopain.app.database.repository;

import codes.nopain.nopain.app.database.document.VerificationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRequestsRepository extends MongoRepository<VerificationRequest, String> {
}
