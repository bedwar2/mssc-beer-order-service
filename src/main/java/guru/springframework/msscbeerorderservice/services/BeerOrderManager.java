package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import sfg.brewery.model.events.AllocationResult;
import sfg.brewery.model.events.ValidateOrderResult;

import java.util.UUID;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void setValidationState(ValidateOrderResult validateOrderResult);

    void setAllocationState(AllocationResult allocationResult);

    void setPickupState(UUID beerOrderID);
}
