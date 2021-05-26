package guru.springframework.msscbeerorderservice.web.controllers;

import guru.springframework.msscbeerorderservice.services.CustomerService;
import sfg.brewery.model.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/customerList")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
       return new ResponseEntity<List<CustomerDto>>(customerService.getAllCustomers(), HttpStatus.OK);
    }
}
