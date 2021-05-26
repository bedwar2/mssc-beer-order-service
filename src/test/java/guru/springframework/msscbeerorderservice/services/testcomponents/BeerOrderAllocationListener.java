package guru.springframework.msscbeerorderservice.services.testcomponents;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImplIT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import sfg.brewery.model.events.AllocateOrderRequest;
import sfg.brewery.model.events.AllocationResult;
import sfg.brewery.model.events.ValidateBeerOrderRequest;
import sfg.brewery.model.events.ValidateOrderResult;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER)
    public void list(Message msg) {

        System.out.println("************** I ran allocate *******************************");

        AllocateOrderRequest result = (AllocateOrderRequest) msg.getPayload();
        result.getBeerOrderDto().getBeerOrderLines().forEach(bol -> {
            bol.setQuantityAllocated(bol.getOrderQuantity());
        });

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT,
                AllocationResult.builder().allocationError(isAllocationError(result.getBeerOrderDto().getCustomerRef()))
                        .pendingInventory(isPartialAllocation(result.getBeerOrderDto().getCustomerRef()))
                        .beerOrderDto(result.getBeerOrderDto()).build());

    }

    public Boolean isAllocationError(String customerRef) {
        if (customerRef.equalsIgnoreCase(BeerOrderManagerImplIT.FAIL_ALLOCATION)) {
            return true;
        }
        return false;
    }

    public Boolean isPartialAllocation(String customerRef) {
        if (customerRef.equalsIgnoreCase(BeerOrderManagerImplIT.PARTIAL_ALLOCATION)) {
            return true;
        }
        return false;
    }



}
