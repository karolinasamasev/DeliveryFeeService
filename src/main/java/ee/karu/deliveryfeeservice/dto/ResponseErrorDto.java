package ee.karu.deliveryfeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ResponseErrorDto {
    private String message;
    private int status;
}
