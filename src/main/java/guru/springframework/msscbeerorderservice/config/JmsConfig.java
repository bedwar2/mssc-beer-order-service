package guru.springframework.msscbeerorderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String BEER_ORDER_QUEUE = "beer-order-queue";
    public static final String BEER_SERVICE_QUEUE = "beer-service-queue";
    public static final String  VALIDATE_ORDER = "validate-order";
    public static final String VALIDATE_ORDER_RESULT = "validate-order-result";
    public static final String  ALLOCATE_ORDER = "allocate-order";
    public static final String  ALLOCATE_ORDER_RESULT = "allocate-order";
    public static final String  ALLOCATE_ORDER_FAILED = "allocate-order-failed";


    @Bean
    public MappingJackson2MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }
}
