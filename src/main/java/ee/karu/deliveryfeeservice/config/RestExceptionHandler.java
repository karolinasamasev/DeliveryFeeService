package ee.karu.deliveryfeeservice.config;

import ee.karu.deliveryfeeservice.dto.ResponseErrorDto;
import ee.karu.deliveryfeeservice.exception.ExtremeWeatherConditionException;
import ee.karu.deliveryfeeservice.exception.NoSuchObservationDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExtremeWeatherConditionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDto handleException(ExtremeWeatherConditionException e, ServletWebRequest request) {
        String endpoint = request.getRequest().getServletPath();
        String parameters = formatParameters(request);
        log.info("{}[{}] occured when calling {} with {}",
                e.getClass().getSimpleName(), e.getMessage(), endpoint, parameters
        );
        return ResponseErrorDto.of(e.getMessage(), HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(NoSuchObservationDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDto handleException(NoSuchObservationDataException e, ServletWebRequest request) {
        String endpoint = request.getRequest().getServletPath();
        String parameters = formatParameters(request);
        log.info("{}[{}] occured when calling {} with {}",
                e.getClass().getSimpleName(), e.getMessage(), endpoint, parameters
        );
        return ResponseErrorDto.of(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    private String formatParameters(ServletWebRequest request) {
        return request.getParameterMap().entrySet()
                .stream()
                .map(entry -> "%s=%s".formatted(entry.getKey(), Arrays.toString(entry.getValue())))
                .collect(joining(", "));
    }

}
