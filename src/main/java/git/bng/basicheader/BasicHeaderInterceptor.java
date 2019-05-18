package git.bng.basicheader;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class BasicHeaderInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(BasicHeaderInterceptor.class);
    private final Properties properties;
    private final ObjectMapper objectMapper;

    public BasicHeaderInterceptor(Properties properties, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Starting Basic Header Authentication Process");
        final String[] clientCredentials = parseClientCredentials(request);

        if (clientCredentials.length == 0) {
            log.info("Rejecting: no basic header provided.");
            writeUnauthorizedResponse("Missing Client Credentials", response);
            return false;
        }

        if (clientCredentials.length < 2 || !properties.getClientId().equals(clientCredentials[0]) || !properties.getClientSecret().equals(clientCredentials[1])) {
            log.info("Rejecting: incorrect basic header provided.");
            writeUnauthorizedResponse("Invalid Client Credentials", response);
            return false;
        }

        log.info("successfully verified basic header.");
        return true;
    }

    private String[] parseClientCredentials(HttpServletRequest request) {
        final Enumeration<String> authHeaders = request.getHeaders("Authorization");
        while (authHeaders.hasMoreElements()) {
            final String header = authHeaders.nextElement();
            if (header.contains("Basic")) {
                final String[] decoded = new String(Base64.decodeBase64(header.replace("Basic ", ""))).split(":");
                return decoded;
            }
        }
        return new String[]{};
    }

    private void writeUnauthorizedResponse(final String message, final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new Error(message)));
    }

    private class Error {
        private final HttpStatus status = HttpStatus.UNAUTHORIZED;
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        @JsonGetter
        public String getStatus() {
            return status.value() + " " + status.getReasonPhrase();
        }

        @JsonGetter
        public String getMessage() {
            return message;
        }
    }
}
