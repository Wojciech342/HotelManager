package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.Role;
import pl.wojtek.project.model.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
