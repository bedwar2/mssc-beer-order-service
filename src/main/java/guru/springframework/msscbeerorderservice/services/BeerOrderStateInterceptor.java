package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEventEnum;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderStateInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final BeerOrderRepository beerOrderRepository;



    @Override
    @Transactional
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(BeerOrderManagerImpl.BEER_ORDER_HEADER, " ")))
                    .ifPresent(beerOrderId -> {
                        BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(beerOrderId)).get();
                        log.info("Setting state to " + state.getId());
                        beerOrder.setOrderStatus(state.getId());
                        beerOrderRepository.saveAndFlush(beerOrder);
                    });
    }

}
