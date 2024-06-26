package ua.team3.carsharingservice.config;

import static org.springframework.security.config.Elements.JWT;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi30Config {
    private static final String BEARER_AUTH = "BearerAuth";
    private static final String BEARER = "bearer";
    private final String apiTitle;
    private final String apiVersion;

    public OpenApi30Config(
            @Value("${openapi.module-name}") String apiTitle,
            @Value("${openapi.api-version}") String apiVersion) {
        this.apiTitle = apiTitle;
        this.apiVersion = apiVersion;
    }

    @Bean
    public OpenAPI customOpenApi() {
        PathItem pathItemForPost = new PathItem();
        PathItem pathItemForGet = new PathItem();
        Operation operation = new Operation();
        operation.setSecurity(new ArrayList<>());
        pathItemForPost.post(operation);
        pathItemForGet.get(operation);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(BEARER)
                                .bearerFormat(JWT)))
                .info(new Info().title(apiTitle).version(apiVersion))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .path("/auth/registration", pathItemForPost)
                .path("/auth/login", pathItemForPost)
                .path("/cars", pathItemForGet)
                .path("/cars/{carId}", pathItemForGet)
                .path("/payments/success", pathItemForGet)
                .path("/payments/cancel", pathItemForGet)
                .path("/stripe/webhook", pathItemForPost);
    }
}
