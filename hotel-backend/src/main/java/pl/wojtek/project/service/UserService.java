package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.wojtek.project.model.AppUser;
import pl.wojtek.project.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getUserById(Long id) {
        AppUser user = userRepository.findById(id).orElse(null);
        return user;
    }

    public List<AppUser> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        return users;
    }

    public AppUser createUser(AppUser user) {
        AppUser savedUser = userRepository.save(user);
        return savedUser;
    }
}
