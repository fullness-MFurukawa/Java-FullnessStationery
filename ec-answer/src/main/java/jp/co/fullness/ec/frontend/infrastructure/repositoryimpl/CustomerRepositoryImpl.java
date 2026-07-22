package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.CustomerAdapter;
import jp.co.fullness.ec.frontend.infrastructure.entity.CustomerEntity;
import jp.co.fullness.ec.frontend.infrastructure.mapper.CustomerMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;
    private final CustomerAdapter customerAdapter;

    @Override
    public void register(Customer customer) {
        CustomerEntity entity = customerAdapter.fromDomain(customer);
        customerMapper.insert(entity);
        customer.setId(entity.getId());   
    }

    @Override
    public Optional<Customer> findByMailAddress(String mailAddress) {
        CustomerEntity entity = customerMapper.selectByMailAddress(mailAddress);
        return Optional.ofNullable(entity).map(customerAdapter::toDomain);
    }

    @Override
    public boolean existsByMailAddress(String mailAddress) {
        return customerMapper.countByMailAddress(mailAddress) > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return customerMapper.countByUsername(username) > 0;
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        CustomerEntity entity = customerMapper.selectById(id);
        return Optional.ofNullable(entity).map(customerAdapter::toDomain);
    }
}