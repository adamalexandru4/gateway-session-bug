package ro.adam.gateway.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    @Value("${server.url}")
    URI redirectUri;

    public GlobalErrorWebExceptionHandler(ErrorAttributes ea, ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(ea, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.defaults());
        if (getError(request) instanceof ClientAuthorizationException) {
            return ServerResponse.status(HttpStatus.FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .location(redirectUri)
                    .body(BodyInserters.fromValue(errorPropertiesMap));
        } else {
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorPropertiesMap));
        }
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }
}