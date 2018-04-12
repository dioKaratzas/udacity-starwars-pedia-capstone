package eu.dkaratzas.starwarspedia.api;

import eu.dkaratzas.starwarspedia.models.Category;
import eu.dkaratzas.starwarspedia.models.Film;
import eu.dkaratzas.starwarspedia.models.People;
import eu.dkaratzas.starwarspedia.models.Planet;
import eu.dkaratzas.starwarspedia.models.Root;
import eu.dkaratzas.starwarspedia.models.Species;
import eu.dkaratzas.starwarspedia.models.Starship;
import eu.dkaratzas.starwarspedia.models.Vehicle;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StarWarsService {
    @GET("")
    Call<Root> getRootUrls();

    @GET("people/")
    Call<Category<People>> getAllPeople(@Query("page") int page);

    @GET("people/{id}/")
    Call<People> getPeople(@Path("id") int peopleId);

    @GET("films/")
    Call<Category<Film>> getAllFilms(@Query("page") int page);

    @GET("films/{id}/")
    Call<Film> getFilm(@Path("id") int filmId);

    @GET("starships")
    Call<Category<Starship>> getAllStarships(@Query("page") int page);

    @GET("starships/{id}/")
    Call<Starship> getStarship(@Path("id") int starshipId);

    @GET("vehicles/")
    Call<Category<Vehicle>> getAllVehicles(@Query("page") Integer page);

    @GET("vehicles/{id}/")
    Call<Vehicle> getVehicle(@Path("id") int vehicleId);

    @GET("species/")
    Call<Category<Species>> getAllSpecies(@Query("page") int page);

    @GET("species/{id}/")
    Call<Species> getSpecies(@Path("id") int speciesId);

    @GET("planets/")
    Call<Category<Planet>> getAllPlanets(@Query("page") Integer page);

    @GET("planets/{id}/")
    Call<Planet> getPlanet(@Path("id") int planetId);

    public enum SwapiCategory {
        FILMS,
        PLANETS,
        SPECIES,
        STARSHIPS,
        VEHICLES,
        PEOPLE
    }

}
