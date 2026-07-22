package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.infrastructure.entity.CustomerEntity;

@Component
public class CustomerAdapter implements Adapter<Customer, CustomerEntity> {

    @Override
    public Customer toDomain(CustomerEntity entity) {
        return Customer.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameKana(entity.getNameKana()) 
                .address1(entity.getAddress1())
                .address2(entity.getAddress2())
                .phoneNumber(entity.getPhoneNumber())
                .mailAddress(entity.getMailAddress())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public CustomerEntity fromDomain(Customer domain) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setNameKana(domain.getNameKana()); 
        entity.setAddress1(domain.getAddress1());
        entity.setAddress2(domain.getAddress2());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setMailAddress(domain.getMailAddress());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
