package ee.karu.deliveryfeeservice.config;

import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.VehicleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClient;

import static java.util.stream.Collectors.joining;

@Slf4j
@Configuration
public class RestConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .messageConverters(httpMessageConverters -> httpMessageConverters.add(new MappingJackson2XmlHttpMessageConverter()))
                .requestInterceptor((HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
                    logRequest(request);
                    return execution.execute(request, body);
                })
                .build();
    }

    private void logRequest(HttpRequest request) {
        String headers = request.getHeaders().entrySet().stream()
                .map(e -> "%s: %s".formatted(e.getKey(), e.getValue()))
                .collect(joining(System.lineSeparator()));
        String message = """
                Sending request: %s %s
                Headers:
                %s
                """.formatted(request.getMethod(), request.getURI(), headers);
        log.debug(message);
    }

    @Bean
    public Converter<String, City> cityConverter() {
        return new Converter<>() {
            @Override
            public City convert(String source) {
                return City.valueOf(source.trim().toUpperCase());
            }
        };
    }


    @Bean
    public Converter<String, VehicleType> vehicleTypeConverter() {
        return new Converter<>() {
            @Override
            public VehicleType convert(String source) {
                return VehicleType.valueOf(source.trim().toUpperCase());
            }
        };
    }

}
