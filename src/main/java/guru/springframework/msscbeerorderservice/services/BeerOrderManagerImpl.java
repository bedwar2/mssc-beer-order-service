package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEventEnum;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import sfg.brewery.model.BeerOrderDto;
import sfg.brewery.model.events.AllocationResult;
import sfg.brewery.model.events.ValidateOrderResult;

import javax.swing.plaf.nimbus.State;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderStateInterceptor beerOrderStateInterceptor;

    public static String BEER_ORDER_HEADER = "beer_order_id";

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);

        log.info("I saved a new Beer: " + savedBeerOrder.getId());
        return savedBeerOrder;
    }

    @Transactional
    @Override
    public void setValidationState(ValidateOrderResult validateOrderResult) {
        log.info("Now I am trying to get the beer: " + validateOrderResult.getBeerOrderId());
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(validateOrderResult.getBeerOrderId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            log.info("setValidationState: Got Beer with ID: " + beerOrder.getId());
            if (validateOrderResult.getIsValid()) {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);

                //Fetch new version since a side effect changed the beerOrder
                BeerOrder validatedOrder = beerOrderRepository.findById(beerOrder.getId()).get();
                sendBeerOrderEvent(validatedOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
            } else {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
            }

        }, () -> log.error("setValidationState - Id not found: " + validateOrderResult.getBeerOrderId()));
    }

    @Override
    public void setAllocationState(AllocationResult allocationResult) {
        BeerOrder beerOrder = beerOrderRepository.findById(allocationResult.getBeerOrderDto().getId()).get();

        if (allocationResult.getAllocationError()) {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
        } else if (allocationResult.getPendingInventory()) {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
            updateAllocatedQty(allocationResult.getBeerOrderDto());
        } else {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
            updateAllocatedQty(allocationResult.getBeerOrderDto());
        }

    }

    @Override
    public void setPickupState(UUID beerOrderID) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderID);

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.BEERORDER_PICKED_UP);
        }, () -> log.error("Not found: " + beerOrderID));
    }

    private void updateAllocatedQty(BeerOrderDto beerOrderDto) {
        BeerOrder allocatedOrder = beerOrderRepository.findById(beerOrderDto.getId()).get();

        allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
            beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                if (beerOrderLine.getId().equals(beerOrderLineDto.getId()))
                    beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
            });
        });

        beerOrderRepository.saveAndFlush(allocatedOrder);
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                        .setHeader(BEER_ORDER_HEADER, beerOrder.getId().toString())
                        .build();

        sm.sendEvent(msg);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma ->{
                    sma.addStateMachineInterceptor(beerOrderStateInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null,null,null));
                });
        sm.start();

        return sm;
    }
}
