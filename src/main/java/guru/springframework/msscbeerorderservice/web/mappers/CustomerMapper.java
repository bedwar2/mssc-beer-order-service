package guru.springframework.msscbeerorderservice.web.mappers;

import guru.springframework.msscbeerorderservice.domain.Customer;
import sfg.brewery.model.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    @Mapping(target = "name", source = "customerName", defaultValue = "undefined")
    CustomerDto customerToCustomerDto(Customer customer);

    @Mapping(target = "customerName", source = "name", defaultValue = "undefined")
    Customer customerDtoToCustomer(CustomerDto customerDto);

}
