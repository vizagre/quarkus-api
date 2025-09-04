package br.com.estudo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("starwars")
public class StarWarsResource {

    @RestClient
    StarWarsService starWarsService;

    @GET
    @Path("starships")
    public String getStarShips(){
        return starWarsService.getStarships();
    }
}
