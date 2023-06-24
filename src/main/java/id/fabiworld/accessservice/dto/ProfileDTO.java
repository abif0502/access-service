package id.fabiworld.accessservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("full_name")
    private String fullName;
}
