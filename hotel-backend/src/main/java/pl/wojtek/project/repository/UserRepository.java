package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

}
