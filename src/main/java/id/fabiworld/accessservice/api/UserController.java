package id.fabiworld.accessservice.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.fabiworld.accessservice.domain.Role;
import id.fabiworld.accessservice.domain.User;
import id.fabiworld.accessservice.dto.ErrorData;
import id.fabiworld.accessservice.dto.ProfileDTO;
import id.fabiworld.accessservice.dto.RoleToUserDTO;
import id.fabiworld.accessservice.dto.request.RegistrationDTO;
import id.fabiworld.accessservice.service.UserService;
import id.fabiworld.accessservice.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/get-all")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO registrationDTO){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user").toUriString());
        try{
            var errorMessage = registrationDTO.validate();
            if(errorMessage.isEmpty()){
                var userData = userService.saveUser(registrationDTO.convertToUser());
                return ResponseEntity.created(uri).body(userData);
            }
            Map<String,Map<String,String>> responseValidation = new HashMap<>();
            responseValidation.put("validation_message", errorMessage);
            return ResponseEntity.badRequest().body(responseValidation);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(e.getMessage()));
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getUserByUsername(@PathParam("username") String username){
        try {
            var user = userService.getUserByUsername(username);
            ProfileDTO userProfile = ProfileDTO.builder()
                    .username(username)
                    .fullName(user.getFullName())
                    .build();
            return ResponseEntity.ok(userProfile);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorData(e.getMessage()));
        }
    }

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/role").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/add-to-user")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO){
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                JWTVerifier verifier = JWT.require(TokenUtil.ALGORITHM).build();
                DecodedJWT decodedJWT = verifier.verify(authorizationHeader.substring("Bearer ".length()));

                String username = decodedJWT.getSubject();
                User user = userService.getUserByUsername(username);
                String accessToken = TokenUtil.generateAccessToken(user.getUsername(),request.getRequestURL().toString(),
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
                String refreshToken = TokenUtil.generateRefreshToken(user.getUsername(),request.getRequestURL().toString());

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (Exception e){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", "Refresh token is missing");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }

    }
}
