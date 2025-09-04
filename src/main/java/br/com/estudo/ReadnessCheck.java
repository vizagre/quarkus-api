package br.com.estudo;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
public class ReadnessCheck  implements HealthCheck {

    @RestClient
    StarWarsService starWarsService;

    @Override
    public HealthCheckResponse call() {
        if (starWarsService.getStarships().contains(StarWarsService.MSG_ERROR)) {
            return HealthCheckResponse.down("ERROR");
        } else {
            return HealthCheckResponse.up("OK");
        }
    }
}