package eu.dkaratzas.starwarspedia.api;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.security.InvalidParameterException;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.models.Category;
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

    private <T> Call getResourceById(int id, SwapiCategory swapiCategory, final ApiCallback<T> apiCallback) {
        Call<T> call = null;

        switch (swapiCategory) {
            case FILMS:
                call = (Call<T>) swapiService.getFilm(id);
                break;
            case PEOPLE:
                call = (Call<T>) swapiService.getPeople(id);
                break;
            case PLANETS:
                call = (Call<T>) swapiService.getPlanet(id);
                break;
            case SPECIES:
                call = (Call<T>) swapiService.getSpecies(id);
                break;
            case VEHICLES:
                call = (Call<T>) swapiService.getVehicle(id);
                break;
            case STARSHIPS:
                call = (Call<T>) swapiService.getStarship(id);
                break;
        }

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                apiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Logger.d("Request was cancelled");
                    apiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    apiCallback.onResponse(null);
                }
            }
        });

        return call;
    }

    private <T> Call getResourcesOfCategory(int page, SwapiCategory swapiCategory, final ApiCallback<T> apiCallback) {
        if (page < 1) {
            throw new InvalidParameterException("Page must be a number starting from 1");
        }

        Call<T> call = null;

        switch (swapiCategory) {
            case FILMS:
                call = (Call<T>) swapiService.getAllFilms(page);
                break;
            case PEOPLE:
                call = (Call<T>) swapiService.getAllPeople(page);
                break;
            case PLANETS:
                call = (Call<T>) swapiService.getAllPlanets(page);
                break;
            case SPECIES:
                call = (Call<T>) swapiService.getAllSpecies(page);
                break;
            case VEHICLES:
                call = (Call<T>) swapiService.getAllVehicles(page);
                break;
            case STARSHIPS:
                call = (Call<T>) swapiService.getAllStarships(page);
                break;
        }

        call.enqueue(new Callback<T>() {
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

        return call;
    }

    public <T> Call getPeopleById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.PEOPLE, apiCallback);
    }

    public <T> Call getAllPeopleAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.PEOPLE, apiCallback);
    }

    public <T> Call getFilmById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.FILMS, apiCallback);
    }

    public <T> Call getAllFilmsAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.FILMS, apiCallback);
    }

    public <T> Call getStarshipById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.STARSHIPS, apiCallback);
    }

    public <T> Call getAllStarshipsAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.STARSHIPS, apiCallback);
    }

    public <T> Call getVehicleById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.VEHICLES, apiCallback);
    }

    public <T> Call getAllVehiclesAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.VEHICLES, apiCallback);
    }

    public <T> Call getSpeciesById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.SPECIES, apiCallback);
    }

    public <T> Call getAllSpeciesAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.SPECIES, apiCallback);
    }

    public <T> Call getPlanetById(int id, ApiCallback<T> apiCallback) {
        return getResourceById(id, SwapiCategory.PLANETS, apiCallback);
    }

    public <T> Call getAllPlanetsAtPage(int page, ApiCallback<Category<T>> apiCallback) {
        return getResourcesOfCategory(page, SwapiCategory.PLANETS, apiCallback);
    }
}

