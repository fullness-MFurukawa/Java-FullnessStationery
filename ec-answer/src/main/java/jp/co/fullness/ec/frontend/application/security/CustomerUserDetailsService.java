package jp.co.fullness.ec.frontend.application.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByMailAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりません: " + email));
        return new CustomerUserDetails(customer);
    }
}