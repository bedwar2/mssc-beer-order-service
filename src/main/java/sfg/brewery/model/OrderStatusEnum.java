package sfg.brewery.model;


//Note: no longer needed
@Deprecated
public enum OrderStatusEnum {
    NEW, VALIDATED, VALIDATION_EXCEPTION, ALLOCATED, ALLOCATION_EXCEPTION,
    PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION
}
