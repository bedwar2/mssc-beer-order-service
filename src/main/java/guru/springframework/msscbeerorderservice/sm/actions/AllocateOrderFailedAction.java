package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEventEnum;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import sfg.brewery.model.events.AllocationFailure;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderFailedAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;


    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = stateContext.getMessageHeaders().get(BeerOrderManagerImpl.BEER_ORDER_HEADER).toString();
        log.info("Beer order failed allocation logging " + beerOrderId);
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_FAILED, AllocationFailure.builder().orderId(UUID.fromString(beerOrderId)).build());

    }
}
