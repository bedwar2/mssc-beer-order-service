package sfg.brewery.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sfg.brewery.model.BeerOrderDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ValidateBeerOrderRequest {
    private BeerOrderDto beerOrderDto;
}
