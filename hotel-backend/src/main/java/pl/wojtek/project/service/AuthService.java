package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojtek.project.exception.ResourceAlreadyExistsException;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.message.request.LoginRequest;
import pl.wojtek.project.message.request.RegisterRequest;
import pl.wojtek.project.message.response.JwtResponse;
import pl.wojtek.project.model.Role;
import pl.wojtek.project.model.RoleName;
import pl.wojtek.project.model.User;
import pl.wojtek.project.repository.RoleRepository;
import pl.wojtek.project.repository.UserRepository;
import pl.wojtek.project.security.jwt.JwtProvider;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            DaoAuthenticationProvider authenticationProvider,
            JwtProvider jwtProvider,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationProvider = authenticationProvider;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        // authenticationProvider uses UserDetailsServiceImpl to fetch user details
        // It calls loadUserByUsername to checks if exists and load the user data (roles included)
        // then hashes the password and checks if it matches the hashed password from the database
        // The userDetailsService is set in WebSecurityConfig
        // if it would not be set an exception would be thrown
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities());
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", registerRequest.getUsername());
        }

        User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(adminRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);
    }

    public User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}