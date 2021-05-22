package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.web.model.CustomerDto;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();
}
