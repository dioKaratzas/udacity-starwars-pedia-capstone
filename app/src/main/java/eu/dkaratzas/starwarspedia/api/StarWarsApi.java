package eu.dkaratzas.starwarspedia.api;

import com.orhanobut.logger.Logger;

import java.io.Serializable;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.models.People;
import eu.dkaratzas.starwarspedia.models.SWModelList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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

    public void getPeople(int peopleId, final ApiCallback<People> apiCallback) {
        Call<People> call = swapiService.getPeople(peopleId);

        call.enqueue(new Callback<People>() {
            @Override
            public void onResponse(Call<People> call, Response<People> response) {
                apiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<People> call, Throwable t) {
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

    public void getPeopleAtPage(final int page, final ApiCallback<SWModelList<People>> apiCallback) {
        Call<SWModelList<People>> call = swapiService.getAllPeople(page);

        call.enqueue(new Callback<SWModelList<People>>() {
            @Override
            public void onResponse(Call<SWModelList<People>> call, Response<SWModelList<People>> response) {
                apiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<People>> call, Throwable t) {
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


    public <T> void getCallPage(int id, T type, final ApiCallback<T> apiCallback) {
        Call<T> call = null;
        if (type instanceof People) {
            call = (Call<T>) swapiService.getPeople(id);
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

