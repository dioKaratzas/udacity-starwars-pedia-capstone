package eu.dkaratzas.starwarspedia.api;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.models.Category;
import eu.dkaratzas.starwarspedia.models.Film;
import eu.dkaratzas.starwarspedia.models.People;
import eu.dkaratzas.starwarspedia.models.Planet;
import eu.dkaratzas.starwarspedia.models.Species;
import eu.dkaratzas.starwarspedia.models.Starship;
import eu.dkaratzas.starwarspedia.models.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class StarWarsApi implements Serializable {

    private static volatile StarWarsApi sharedInstance = new StarWarsApi();
    private SwapiService swapiService;

    private StarWarsApi() {
        //Prevent from the reflection api.
        if (sharedInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        swapiService = retrofit.create(SwapiService.class);
    }

    public static StarWarsApi getApi() {
        if (sharedInstance == null) {
            synchronized (StarWarsApi.class) {
                if (sharedInstance == null) sharedInstance = new StarWarsApi();
            }
        }

        return sharedInstance;
    }

    private RequestType getResourceById(int id, SwapiCategory swapiCategory) {

        switch (swapiCategory) {
            case FILMS:
                return new RequestType(swapiService.getFilm(id));
            case PEOPLE:
                return new RequestType(swapiService.getPeople(id));
            case PLANETS:
                return new RequestType(swapiService.getPlanet(id));
            case SPECIES:
                return new RequestType(swapiService.getSpecies(id));
            case VEHICLES:
                return new RequestType(swapiService.getVehicle(id));
            case STARSHIPS:
                return new RequestType(swapiService.getStarship(id));
        }

        return null;
    }

    private RequestType getResourcesOfCategoryOnPage(int page, SwapiCategory swapiCategory) {
        if (page < 1) {
            throw new InvalidParameterException("Page must be a number starting from 1");
        }

        switch (swapiCategory) {
            case FILMS:
                return new RequestType(swapiService.getAllFilms(page));
            case PEOPLE:
                return new RequestType(swapiService.getAllPeople(page));
            case PLANETS:
                return new RequestType(swapiService.getAllPlanets(page));
            case SPECIES:
                return new RequestType(swapiService.getAllSpecies(page));
            case VEHICLES:
                return new RequestType(swapiService.getAllVehicles(page));
            case STARSHIPS:
                return new RequestType(swapiService.getAllStarships(page));
        }
        return null;
    }

    public RequestType<People> getPeopleById(int id) {
        return getResourceById(id, SwapiCategory.PEOPLE);
    }

    public RequestType<Category<People>> getAllPeopleAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.PEOPLE);
    }

    public RequestType<Film> getFilmById(int id) {
        return getResourceById(id, SwapiCategory.FILMS);
    }

    public RequestType<Category<Film>> getAllFilmsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.FILMS);
    }

    public RequestType<Starship> getStarshipById(int id) {
        return getResourceById(id, SwapiCategory.STARSHIPS);
    }

    public RequestType<Category<Starship>> getAllStarshipsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.STARSHIPS);
    }

    public RequestType<Vehicle> getVehicleById(int id) {
        return getResourceById(id, SwapiCategory.VEHICLES);
    }

    public RequestType<Category<Vehicle>> getAllVehiclesAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.VEHICLES);
    }

    public RequestType<Species> getSpeciesById(int id) {
        return getResourceById(id, SwapiCategory.SPECIES);
    }

    public RequestType<Category<Species>> getAllSpeciesAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.SPECIES);
    }

    public RequestType<Planet> getPlanetById(int id) {
        return getResourceById(id, SwapiCategory.PLANETS);
    }

    public RequestType<Category<Planet>> getAllPlanetsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.PLANETS);
    }

    public class RequestType<T> {
        private Call<T> delegate;

        public RequestType(Call<T> delegate) {
            this.delegate = delegate;
        }

        public T sync() {
            try {
                return delegate.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public Call<T> async(final StarWarsApiCallback<T> apiCallback) {
            delegate.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    apiCallback.onResponse(response.body());
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    if (call.isCanceled()) {
                        Logger.d("Request was cancelled");
                        apiCallback.onCancel();
                    } else {
                        Logger.e(t.getMessage());
                        apiCallback.onResponse(null);
                    }
                }
            });

            return delegate;
        }
    }
}

