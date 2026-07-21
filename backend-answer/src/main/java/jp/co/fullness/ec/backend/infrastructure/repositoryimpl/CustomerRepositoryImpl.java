package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.CustomerAdapter;
import jp.co.fullness.ec.backend.infrastructure.mapper.CustomerMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;
    private final CustomerAdapter customerAdapter;

    @Override
    public List<Customer> findAll() {
        return customerAdapter.toDomainList(customerMapper.selectAll());
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return Optional.of(customerAdapter.toDomain(customerMapper.selectById(id)));
    }
}