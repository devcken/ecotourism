package com.kakaopay.ecotourism.auth;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService implements ApplicationContextAware, InitializingBean, UserDetailsService {
    @NonNull private final AccountRepository accountRepository;

    private AuthorizationServerTokenServices tokenServices;

    private PasswordEncoder passwordEncoder;

    private ApplicationContext context;

    @Transactional
    public Pair<Account, OAuth2AccessToken> create(String username, String password) {
        val account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));

        Set<GrantedAuthority> authorities = Arrays.stream(account.getAuthorities().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        Map<String, String> params = new HashMap<>();

        Set<String> scopes = new HashSet<>();
        scopes.add("read");

        Set<String> resourceIds = new HashSet<>();

        Set<String> responseTypes = new HashSet<>();
        responseTypes.add("code");

        Map<String, Serializable> extensionProperties = new HashMap<>();

        OAuth2Request oAuth2Request = new OAuth2Request(params, "client", authorities, true, scopes,
                resourceIds, "", responseTypes, extensionProperties);

        User user = new User(account.getUsername(), account.getPassword(), true, true, true, true, authorities);

        OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, new UsernamePasswordAuthenticationToken(user, null, authorities));

        OAuth2AccessToken accessToken = tokenServices.createAccessToken(auth);

        return Pair.of(accountRepository.save(account), accessToken);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return accountRepository.findById(username)
                .map(a -> User.builder().username(a.getUsername()).password(a.getPassword()).authorities(a.getAuthorities().split(",")).build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        passwordEncoder = context.getBean(PasswordEncoder.class);
        tokenServices = context.getBean(AuthorizationServerTokenServices.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
