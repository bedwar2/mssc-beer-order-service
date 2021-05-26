package guru.springframework.msscbeerorderservice.services;

import sfg.brewery.model.CustomerDto;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();
}
