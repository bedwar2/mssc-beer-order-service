package guru.springframework.msscbeerorderservice.services.beer;

import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;

public interface BeerService {
    BeerDto GetBeerByUpc(String upc);
}
