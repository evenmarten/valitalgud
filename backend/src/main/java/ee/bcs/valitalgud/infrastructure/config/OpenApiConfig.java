package ee.bcs.valitalgud.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI valitalgudOpenAPI() {
        return new OpenAPI().info(buildApiInfo());
    }

    private Info buildApiInfo() {
        return new Info()
                .title("Valitalgud API")
                .version("0.0.1")
                .description("""
                        Ürituste halduse rakenduse REST API.

                        Märkus: LandingPage (`/`) on puhtalt frontendi staatiline vaade — \
                        sellele ei vasta ühtegi backend endpointi. Vaata `docs/tasks/backend/LandingPage/LandingPage.md`.
                        """);
    }
}
