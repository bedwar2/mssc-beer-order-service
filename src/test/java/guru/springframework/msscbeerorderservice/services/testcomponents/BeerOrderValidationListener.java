package guru.springframework.msscbeerorderservice.services.testcomponents;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import sfg.brewery.model.BeerOrderDto;
import sfg.brewery.model.events.ValidateBeerOrderRequest;
import sfg.brewery.model.events.ValidateOrderResult;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER)
    public void list(Message msg) throws InterruptedException {

        Boolean isValid = true;
        System.out.println("************** I ran *******************************");

        if (((ValidateBeerOrderRequest) msg.getPayload()).getBeerOrderDto().getCustomerRef().equals("failit")) {
            isValid = false;
        }

        Thread.sleep(1000);
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT,
                ValidateOrderResult.builder().isValid(isValid)
                        .beerOrderId(((ValidateBeerOrderRequest) msg.getPayload()).getBeerOrderDto().getId()).build());


    }
}
