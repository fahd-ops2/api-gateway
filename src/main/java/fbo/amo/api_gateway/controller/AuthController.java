package fbo.amo.api_gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> body) {
        logger.info("Received token request with body: {}", body); // Log the request body for debugging

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Convert the received JSON body into a form-urlencoded format
        StringBuilder formBody = new StringBuilder();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            formBody.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }

        // Remove the trailing "&"
        if (formBody.length() > 0) {
            formBody.setLength(formBody.length() - 1);
        }

        // Create the request with form data
        HttpEntity<String> request = new HttpEntity<>(formBody.toString(), headers);

        // Prepare Keycloak token URL
        String keycloakTokenUrl = keycloakUrl + "/realms/amo-realm/protocol/openid-connect/token";

        // Make the request to Keycloak and capture the response
        ResponseEntity<String> response = restTemplate.exchange(
                keycloakTokenUrl, HttpMethod.POST, request, String.class);

        // Return the response from Keycloak
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
