package pl.wojtek.project.message.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RegisterRequest {

    private String username;

    private Set<String> role;

    private String password;
}
