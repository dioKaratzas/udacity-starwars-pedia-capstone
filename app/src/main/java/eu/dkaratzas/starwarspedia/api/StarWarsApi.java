package eu.dkaratzas.starwarspedia.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.loaders.CategoryLoader;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

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

    public ApiLoader<SwapiModelList<SwapiModel>> getAllCategoryItems(SwapiCategory swapiCategory) {
        return new ApiLoader<>(swapiCategory);
    }

    @SuppressWarnings("unchecked")
    public Request<SwapiModel> getCategoryItemById(int id, SwapiCategory swapiCategory) {
        return getItemRequestOnCategoryById(id, SwapiCategory.PEOPLE);
    }

    @SuppressWarnings("unchecked")
    public Request getItemRequestOnCategoryById(int id, SwapiCategory swapiCategory) {

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

    @SuppressWarnings("unchecked")
    public Request<SwapiModelList<SwapiModel>> getItemsRequestOnCategoryById(int page, SwapiCategory swapiCategory) {
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
            } catch (IOException ex) {
                Timber.e(ex);
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
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    apiCallback.onResponse(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        Timber.d("Request was cancelled");
                        apiCallback.onCancel();
                    } else {
                        Timber.e(t);
                        apiCallback.onResponse(null);
                    }
                }
            });

            return delegate;
        }
    }

    public final class ApiLoader<T> {
        private SwapiCategory mSwapiCategory;

        public ApiLoader(SwapiCategory swapiCategory) {
            this.mSwapiCategory = swapiCategory;
        }

        public Loader loaderLoad(Context context, LoaderManager loaderManager, int loaderId, final StarWarsApiCallback<T> apiCallback) {
            return CategoryLoader.reload(context, loaderManager, loaderId, mSwapiCategory, apiCallback);
        }
    }
}

