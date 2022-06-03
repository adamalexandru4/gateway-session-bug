package ro.adam.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Value("${server.url}")
    final String serverUrl;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(
                new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler()
        );

        http.authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .oauth2Login()
                .authenticationFailureHandler(oidcAuthenticationFailureHandler())
                .authorizedClientRepository(authorizedClientRepository())
                .and()
                .logout()
                .logoutHandler(logoutHandler)
                .logoutSuccessHandler(oidcLogoutSuccessHandler());

        return http.build();
    }

    @Bean
    public ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(serverUrl);
        return successHandler;
    }

    @Bean
    public ServerAuthenticationFailureHandler oidcAuthenticationFailureHandler() {
        return new RedirectServerAuthenticationFailureHandler(serverUrl);
    }

    /**
     * https://github.com/spring-projects/spring-security/issues/7889
     */
    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }
}