package codes.nopain.nopain.app.database.repository;

import codes.nopain.nopain.app.database.document.ClassSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesRepository extends MongoRepository<ClassSchedule, String> {
    Optional<ClassSchedule> findByClassname(String className);

    List<ClassSchedule> findAllBySubject(String subject);

    List<ClassSchedule> findAllByGroup(String group);

    List<ClassSchedule> findAllByGroupAndSubject(String group, String subject);
}
