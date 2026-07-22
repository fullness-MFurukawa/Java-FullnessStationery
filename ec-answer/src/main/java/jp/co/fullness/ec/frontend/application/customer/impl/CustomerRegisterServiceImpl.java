package jp.co.fullness.ec.frontend.application.customer.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.frontend.application.customer.CustomerRegisterService;
import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.presentation.form.CustomerRegisterForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerRegisterServiceImpl implements CustomerRegisterService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isMailAddressTaken(String mailAddress) {
        return customerRepository.existsByMailAddress(mailAddress);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return customerRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void register(CustomerRegisterForm form) {
        Customer customer = Customer.builder()
                .name(form.getName())
                .nameKana(form.getNameKana())
                .address1(form.getAddress1())
                .address2(form.getAddress2())
                .phoneNumber(form.getPhoneNumber())
                .mailAddress(form.getMailAddress())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))   // BCrypt
                .build();
        customerRepository.register(customer);
    }
}