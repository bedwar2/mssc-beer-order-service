package guru.springframework.msscbeerorderservice.services.orders;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEventEnum;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.services.BeerOrderManager;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import sfg.brewery.model.events.ValidateOrderResult;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class ValidateOrderResponseListener {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderManager beerOrderManager;

    @Transactional
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT)
    public void listen(@Payload ValidateOrderResult validateOrderResult) {

        beerOrderManager.setValidationState(validateOrderResult);

    }

}
