package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import guru.springframework.msscbeerorderservice.web.mappers.CustomerMapper;
import guru.springframework.msscbeerorderservice.web.model.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::customerToCustomerDto).collect(Collectors.toList());
    }
}
