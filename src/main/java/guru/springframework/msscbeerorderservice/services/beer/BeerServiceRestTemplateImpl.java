package guru.springframework.msscbeerorderservice.services.beer;

import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConfigurationProperties(prefix = "guru.sfg", ignoreUnknownFields = true)
public class BeerServiceRestTemplateImpl implements BeerService {

    public static final String BEER_URL = "/api/v1/beerUpc/";
    private final RestTemplate restTemplate;

    private String beerHost;

    BeerServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public void setBeerHost(String beerHost) {
        this.beerHost  = beerHost;
    }

    @Override
    public BeerDto GetBeerByUpc(String upc) {

        //Method 1 - using exchange
        /*
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<BeerDto> requestEntity = new HttpEntity<BeerDto>(requestHeaders);
        ResponseEntity<BeerDto> beerDto
                = restTemplate.exchange(beerHost + BEER_URL + upc, HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<BeerDto>() {},
                (Object) upc);
        return beerDto.getBody();
        */

        //Method 2
        return restTemplate.getForObject(beerHost + BEER_URL + upc, BeerDto.class);


    }

}
