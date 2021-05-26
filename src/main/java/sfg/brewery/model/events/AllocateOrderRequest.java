package sfg.brewery.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sfg.brewery.model.BeerOrderDto;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllocateOrderRequest {
    private BeerOrderDto beerOrderDto;
}
