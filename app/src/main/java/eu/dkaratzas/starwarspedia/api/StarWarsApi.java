package eu.dkaratzas.starwarspedia.api;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.libs.RetrofitLoader.CustomLoader;
import eu.dkaratzas.starwarspedia.models.Film;
import eu.dkaratzas.starwarspedia.models.People;
import eu.dkaratzas.starwarspedia.models.Planet;
import eu.dkaratzas.starwarspedia.models.Species;
import eu.dkaratzas.starwarspedia.models.Starship;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;
import eu.dkaratzas.starwarspedia.models.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * StarWarsApi Singleton Class
 * Every call returns {@link Request} that gives the ability
 * to run the retrofit calls synchronous or asynchronous.
 */
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

    public Request<People> getPeopleById(int id) {
        return getResourceById(id, SwapiCategory.PEOPLE);
    }

    public Request<SwapiModelList<People>> getAllPeopleAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.PEOPLE);
    }

    public Request<Film> getFilmById(int id) {
        return getResourceById(id, SwapiCategory.FILM);
    }

    public Request<SwapiModelList<Film>> getAllFilmsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.FILM);
    }

    public Request<Starship> getStarshipById(int id) {
        return getResourceById(id, SwapiCategory.STARSHIP);
    }

    public Request<SwapiModelList<Starship>> getAllStarshipsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.STARSHIP);
    }

    public Request<Vehicle> getVehicleById(int id) {
        return getResourceById(id, SwapiCategory.VEHICLE);
    }

    public Request<SwapiModelList<Vehicle>> getAllVehiclesAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.VEHICLE);
    }

    public Request<Species> getSpeciesById(int id) {
        return getResourceById(id, SwapiCategory.SPECIES);
    }

    public Request<SwapiModelList<Species>> getAllSpeciesAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.SPECIES);
    }

    public Request<Planet> getPlanetById(int id) {
        return getResourceById(id, SwapiCategory.PLANET);
    }

    public Request<SwapiModelList<Planet>> getAllPlanetsAtPage(int page) {
        return getResourcesOfCategoryOnPage(page, SwapiCategory.PLANET);
    }

    private Request getResourceById(int id, SwapiCategory swapiCategory) {

        switch (swapiCategory) {
            case FILM:
                return new Request(swapiService.getFilm(id));
            case PEOPLE:
                return new Request(swapiService.getPeople(id));
            case PLANET:
                return new Request(swapiService.getPlanet(id));
            case SPECIES:
                return new Request(swapiService.getSpecies(id));
            case VEHICLE:
                return new Request(swapiService.getVehicle(id));
            case STARSHIP:
                return new Request(swapiService.getStarship(id));
        }

        return null;
    }

    public Request getResourcesOfCategoryOnPage(int page, SwapiCategory swapiCategory) {
        if (page < 1) {
            throw new InvalidParameterException("Page must be a number starting from 1");
        }

        switch (swapiCategory) {
            case FILM:
                return new Request(swapiService.getAllFilms(page));
            case PEOPLE:
                return new Request(swapiService.getAllPeople(page));
            case PLANET:
                return new Request(swapiService.getAllPlanets(page));
            case SPECIES:
                return new Request(swapiService.getAllSpecies(page));
            case VEHICLE:
                return new Request(swapiService.getAllVehicles(page));
            case STARSHIP:
                return new Request(swapiService.getAllStarships(page));
        }
        return null;
    }

    public final class Request<T> {
        private Call<T> delegate;

        public Request(Call<T> delegate) {
            this.delegate = delegate;
        }

        /**
         * Use this method to perform a synchronous retrofit request.
         * Cannot perform synchronous HTTP requests on the main thread.
         * Must get executed on a new {@link Thread} or {@link android.os.AsyncTask}
         *
         * @return response body type
         */
        public T sync() {
            try {
                return delegate.execute().body();
            } catch (IOException e) {
                Logger.e(e.getMessage());
                return null;
            }
        }

        /**
         * Use this method to perform an asynchronous retrofit request.
         * Return {@link Call<T>} which you can use to cancel the request
         *
         * @param apiCallback
         * @return {@link Call<T>}
         */
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

        public void loaderLoad(Context context, LoaderManager loaderManager, SwapiCategory swapiCategory, final StarWarsApiCallback<T> apiCallback) {
            CustomLoader.load(context, loaderManager, 12, swapiCategory, apiCallback);
        }
    }
}

