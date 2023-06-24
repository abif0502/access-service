package id.fabiworld.accessservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorData {
    @JsonProperty("error_message")
    private String errorMessage;
}
