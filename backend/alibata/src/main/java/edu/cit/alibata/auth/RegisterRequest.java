package edu.cit.alibata.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.cit.alibata.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    @JsonProperty("password")
    private String password;
    @Builder.Default
    private boolean subscriptionStatus = false;
    @Builder.Default
    private Role role = Role.USER; 

}
