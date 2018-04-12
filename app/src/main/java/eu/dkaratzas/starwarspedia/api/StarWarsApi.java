package eu.dkaratzas.starwarspedia.api;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.io.Serializable;

import eu.dkaratzas.starwarspedia.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static eu.dkaratzas.starwarspedia.api.StarWarsService.SwapiCategory;

public class StarWarsApi implements Serializable {

    private static volatile StarWarsApi sharedInstance = new StarWarsApi();
    private StarWarsService swapiService;

    private StarWarsApi() {
        //Prevent from the reflection api.
        if (sharedInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        swapiService = retrofit.create(StarWarsService.class);
    }

    public static StarWarsApi getApi() {
        if (sharedInstance == null) {
            synchronized (StarWarsApi.class) {
                if (sharedInstance == null) sharedInstance = new StarWarsApi();
            }
        }

        return sharedInstance;
    }


    public <T> void getResourceById(int id, SwapiCategory swapiCategory, final ApiCallback<T> apiCallback) {
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
                    Logger.e("Request was cancelled");
                    apiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    apiCallback.onResponse(null);
                }
            }
        });
    }

    public <T> void getResourcesOfCategory(int page, SwapiCategory swapiCategory, final ApiCallback<T> apiCallback) {
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
                    Logger.e("Request was cancelled");
                    apiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    apiCallback.onResponse(null);
                }
            }
        });
    }
}

