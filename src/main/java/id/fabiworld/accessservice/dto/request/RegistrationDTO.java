package id.fabiworld.accessservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.fabiworld.accessservice.domain.User;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Data
public class RegistrationDTO {
    @JsonProperty("full_name")
    private String fullName;
    private String username;
    private String email;
    @ToString.Exclude
    private String password;

    public Map<String, String> validate(){
        Map<String,String> errorMessage = new HashMap<>();
        String valUsername = validateUsername();
        String valFullName = validateFullname();
        String valEmail = validateEmail();
        String valPassword = validatePassword();
        if(!valUsername.isEmpty()){
            errorMessage.put("username",valUsername);
        }
        if(!valFullName.isEmpty()){
            errorMessage.put("full_name",valFullName);
        }
        if(!valEmail.isEmpty()){
            errorMessage.put("email",valEmail);
        }
        if(!valPassword.isEmpty()){
            errorMessage.put("password",valPassword);
        }

        return errorMessage;
    }

    public User convertToUser(){
        return User.builder()
                .username(this.username)
                .fullName(this.fullName)
                .email(this.email)
                .password(this.password)
                .build();
    }

    private String validateUsername(){
        if(this.username == null || this.username.equals("")){
            return "tidak boleh kosong";
        }
        if(this.username.length() < 5){
            return "min 5 karakter";
        }
        return "";
    }

    private String validateFullname(){
        if(this.fullName == null || this.fullName.equals("")){
            return "tidak boleh kosong";
        }
        if(this.fullName.length() < 5){
            return "min 5 karakter";
        }
        return "";
    }

    private String validateEmail(){
        if(this.email == null || this.email.equals("")){
            return "tidak boleh kosong";
        }
        if(this.email.length() < 8){
            return "min 8 karakter";
        }
        return "";
    }

    private String validatePassword(){
        if(this.password == null || this.password.equals("")){
            return "tidak boleh kosong";
        }
        if(this.password.length() < 8){
            return "min 8 karakter";
        }
        return "";
    }

}
