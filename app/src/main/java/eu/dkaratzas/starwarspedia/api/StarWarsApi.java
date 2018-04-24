package eu.dkaratzas.starwarspedia.api;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import eu.dkaratzas.starwarspedia.Constants;
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

    public SwapiModelList<SwapiModel> getAllItemsOnCategory(SwapiCategory swapiCategory) {
        int currentPage = 1;
        boolean gotAnotherPage;
        SwapiModelList<SwapiModel> result = null;
        do {
            try {
                result = appendResult(result, getItemsRequestOnCategoryById(currentPage, swapiCategory).sync());
                currentPage++;
                gotAnotherPage = result.gotAnotherPage();
            } catch (Exception ex) {
                Timber.e(Log.getStackTraceString(ex));
                return null;
            }


        } while (gotAnotherPage);

        return result;
    }

    public SwapiModel getItemOnCategoryByUrl(String itemUrl, SwapiCategory swapiCategory) {
        try {
            int itemId = SwapiModel.getIdFromUrl(itemUrl);
            if (itemId != 0) {
                return (SwapiModel) getItemRequestOnCategoryById(itemId, swapiCategory).sync();
            }
        } catch (Exception ex) {
            Timber.e(Log.getStackTraceString(ex));
            return null;
        }
        return null;
    }

    public List<SwapiModel> getItemsOnCategoryByUrls(List<String> itemUrls, SwapiCategory swapiCategory) {
        if (itemUrls != null && itemUrls.size() > 0) {
            try {
                List<SwapiModel> resultList = new ArrayList<>();

                for (String item : itemUrls) {

                    int itemId = SwapiModel.getIdFromUrl(item);
                    if (itemId != 0) {
                        SwapiModel result = (SwapiModel) getItemRequestOnCategoryById(itemId, swapiCategory).sync();
                        if (result != null)
                            resultList.add(result);
                    }
                }

                return resultList;
            } catch (Exception ex) {
                Timber.e(Log.getStackTraceString(ex));
                return null;
            }

        }
        return null;
    }

    private SwapiModelList<SwapiModel> appendResult(SwapiModelList<SwapiModel> result, SwapiModelList<SwapiModel> newResult) {
        if (result == null) {
            result = newResult;
        } else {
            result.results.addAll(newResult.results);

            result = new SwapiModelList<>(newResult.getNext(), newResult.getPrevious(), newResult.getCount(), result.results);

        }
        return result;
    }


    @SuppressWarnings("unchecked")
    private Request getItemRequestOnCategoryById(int id, SwapiCategory swapiCategory) {

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
    private Request<SwapiModelList<SwapiModel>> getItemsRequestOnCategoryById(int page, SwapiCategory swapiCategory) {
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

    private final class Request<T> {
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
}

