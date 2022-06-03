package ro.adam.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController  
public class Controller {  
  
	// https://refactorfirst.com/spring-cloud-gateway-keycloak-oauth2-openid-connect.html
	// https://refactorfirst.com/spring-cloud-gateway-keycloak-rbac-resource-server.html
    @GetMapping("/health")  
    public String index(Principal principal) {  
        return principal.getName();
    }

}

