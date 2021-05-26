package sfg.brewery.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sfg.brewery.model.BeerOrderDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeallocateOrderRequest {
    private BeerOrderDto beerOrderDto;
}
