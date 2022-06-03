package ro.adam.gateway.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorAttributes extends DefaultErrorAttributes {

    final Tracer tracer;

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<>();
        if (getError(request) instanceof ClientAuthorizationException) {
            errorAttributes.put("status", HttpStatus.FOUND.value());
            errorAttributes.put("message", "session_expired");
        } else {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

            String traceId = getCurrentTraceId();
            errorAttributes.put("message", "Anumite servicii nu funcționează momentan. " + traceId);
        }
        errorAttributes.put("type", "error");

        return errorAttributes;
    }

    @NonNull
    private String getCurrentTraceId() {
        Span currentSpan = tracer.currentSpan();
        String traceId = "";
        if (currentSpan != null) {
            traceId = "(#" + currentSpan.context().traceId() + ")";
        }
        return traceId;
    }

}
