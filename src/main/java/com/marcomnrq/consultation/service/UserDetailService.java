package com.marcomnrq.consultation.service;

import com.marcomnrq.consultation.domain.model.Role;
import com.marcomnrq.consultation.domain.model.User;
import com.marcomnrq.consultation.domain.repository.UserRepository;
import com.marcomnrq.consultation.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final HttpServletRequest request;

    private final SentinelService sentinelService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        sentinelService.bruteForceCheck(getClientIP());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with username"));

        return new
                org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.getEnabled(),
                true,
                true,
                true,
                getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<Role> userRoles = user.getRoles();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : userRoles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}