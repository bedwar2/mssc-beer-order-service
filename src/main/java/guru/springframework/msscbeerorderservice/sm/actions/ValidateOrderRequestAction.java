package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEventEnum;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import guru.springframework.msscbeerorderservice.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import sfg.brewery.model.events.ValidateBeerOrderRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderRequestAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.BEER_ORDER_HEADER).toString();

        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(beerOrderId));
        if (beerOrder != null) {

            ValidateBeerOrderRequest validateBeerOrderRequest = ValidateBeerOrderRequest.builder().beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder)).build();
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER,
                    validateBeerOrderRequest);

            log.info("Sent validation request to queue for order id " + beerOrder.getId());

        }




    }
}
