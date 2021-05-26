package guru.springframework.msscbeerorderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatusEnum;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import guru.springframework.msscbeerorderservice.services.beer.BeerServiceRestTemplateImpl;
import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;
import guru.springframework.msscbeerorderservice.services.beer.model.BeerPageList;
import guru.springframework.msscbeerorderservice.web.mappers.BeerOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import sfg.brewery.model.events.AllocationFailure;

import javax.transaction.Transactional;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@SpringBootTest
@ExtendWith(WireMockExtension.class)
public class BeerOrderManagerImplIT {
    public static final String FAIL_ALLOCATION = "failAllocation";
    public static final String PARTIAL_ALLOCATION = "partialAllocation";

    @Autowired
    BeerOrderManager beerOrderManager;


    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderMapper beerOrderMapper;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JmsTemplate jmsTemplate;

    Customer testCustomer;

    UUID beerId = UUID.randomUUID();
    UUID beerOrderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder().customerName("Test Customer").build());
    }

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = new WireMockServer(wireMockConfig().port(8089));
            server.start();
            return server;
        }
    }

    @Test
    public void testNewToAllocate() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPageList beerPageList = new BeerPageList(Arrays.asList(beerDto));

        wireMockServer.stubFor(get(BeerServiceRestTemplateImpl.BEER_URL + "12345")
                    .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder  = createBeerOrder();
        beerOrder.setCustomerRef("custRef");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        //savedBeerOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        //assertNotNull(savedBeerOrder);
       // assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());
        beerOrderRepository.findAll().forEach(bo -> {
            System.out.println(bo.toString());
        });

        Thread.sleep(3000);
        savedBeerOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
        beerOrderRepository.findAll().forEach(bo -> {
            System.out.println(bo.toString());
        });
        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());
        //System.out.println(savedBeerOrder.toString());
    }

    @Test
    void testAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPageList beerPageList = new BeerPageList(Arrays.asList(beerDto));

        wireMockServer.stubFor(get(BeerServiceRestTemplateImpl.BEER_URL + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder  = createBeerOrder();
        beerOrder.setCustomerRef("allgood");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        //assertNotNull(savedBeerOrder);
        Thread.sleep(3000);

        BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
        assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());


    }

    @Test
    void testFailedValidation() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPageList beerPageList = new BeerPageList(Arrays.asList(beerDto));

        wireMockServer.stubFor(get(BeerServiceRestTemplateImpl.BEER_URL + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder  = createBeerOrder();
        beerOrder.setCustomerRef("failit");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        //assertNotNull(savedBeerOrder);
        Thread.sleep(3000);

        BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
        assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());


    }



    @Test
    void TestFailedAllocation() throws JsonProcessingException,InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPageList beerPageList = new BeerPageList(Arrays.asList(beerDto));

        wireMockServer.stubFor(get(BeerServiceRestTemplateImpl.BEER_URL + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder  = createBeerOrder();
        beerOrder.setCustomerRef(FAIL_ALLOCATION);

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        //assertNotNull(savedBeerOrder);
        Thread.sleep(3000);

        BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
        assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());

        //NExt check JMS
        AllocationFailure msg = (AllocationFailure) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_ORDER_FAILED);
        System.out.println("Got message off queue: " + msg.getOrderId());

        assertEquals(msg.getOrderId(), savedBeerOrder.getId());
    }

    @Test
    void TestPartialAllocation() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPageList beerPageList = new BeerPageList(Arrays.asList(beerDto));

        wireMockServer.stubFor(get(BeerServiceRestTemplateImpl.BEER_URL + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder  = createBeerOrder();
        beerOrder.setCustomerRef(PARTIAL_ALLOCATION);

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        //assertNotNull(savedBeerOrder);
        Thread.sleep(3000);

        BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
        assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
    }

    public BeerOrder createBeerOrder() {
        BeerOrder beerOrder  = BeerOrder.builder().id(beerOrderId).customer(testCustomer).build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder().beerId(beerId).upc("12345").orderQuantity(1).beerOrder(beerOrder).build());

        beerOrder.setBeerOrderLines(lines);
        return beerOrder;

    }



}
