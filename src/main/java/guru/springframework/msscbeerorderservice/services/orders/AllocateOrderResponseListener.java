package guru.springframework.msscbeerorderservice.services.orders;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.services.BeerOrderManager;
import guru.springframework.msscbeerorderservice.sm.actions.AllocateOrderAction;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sfg.brewery.model.events.AllocationResult;

@Component
@RequiredArgsConstructor
public class AllocateOrderResponseListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESULT)
    public void listen(@Payload AllocationResult allocationResult) {
        beerOrderManager.setAllocationState(allocationResult);
    }
}
