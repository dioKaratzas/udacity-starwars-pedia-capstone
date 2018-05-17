package eu.dkaratzas.starwarspedia.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;

import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import api.AllFilmsQuery;
import api.AllPersonsQuery;
import api.AllPlanetsQuery;
import api.AllSpeciesQuery;
import api.AllStarshipsQuery;
import api.AllVehiclesQuery;
import api.FilmQuery;
import api.PersonQuery;
import api.PlanetQuery;
import api.SpeciesQuery;
import api.StarshipQuery;
import api.VehicleQuery;
import api.type.CustomType;
import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.libs.Tls12SocketFactory;
import eu.dkaratzas.starwarspedia.models.AllQueryData;
import eu.dkaratzas.starwarspedia.models.CategoryItems;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import timber.log.Timber;

/**
 * ApolloManager Singleton Class
 * Every call uses a custom Loaded {@link ApolloLoader} to fetch and deliver the result to controllers
 */
public class ApolloManager implements Serializable {

    private static volatile ApolloManager sharedInstance = new ApolloManager();
    private ApolloClient apolloClient;

    private ApolloManager() {
        // Prevent from the reflection api.
        if (sharedInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {
            @Override
            public Date decode(CustomTypeValue value) {

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                try {
                    Date date = dateFormatParse.parse(value.value.toString());
                    Timber.d("Date ->%s", date);
                    return date;
                } catch (ParseException e) {
                    throw new RuntimeException(e);

                }
            }

            @Override
            public CustomTypeValue encode(Date value) {
                return new CustomTypeValue.GraphQLString(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(value));
            }
        };

        apolloClient = ApolloClient.builder()
                .serverUrl(Constants.BASE_URL)
                .addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
                .okHttpClient(enableTls12OnPreLollipop(new OkHttpClient.Builder()).build())
                .build();
    }

    public static ApolloManager instance() {
        if (sharedInstance == null) {
            synchronized (ApolloManager.class) {
                if (sharedInstance == null) sharedInstance = new ApolloManager();
            }
        }

        return sharedInstance;
    }

    public void fetchSwapiCategory(Context context, SwapiCategory swapiCategory, LoaderManager loaderManager, int loaderId, final StarWarsApiCallback<CategoryItems> apiCallback) {

        ApolloLoader.load(context, loaderManager, loaderId, getApolloCallForCategory(swapiCategory), new ApolloCall.Callback() {
            @Override
            public void onResponse(@Nonnull Response response) {
                apiCallback.onResponse(new CategoryItems(response));
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Timber.e(e);
                apiCallback.onResponse(null);
            }
        });

    }

    public void fetchSwapiItem(final Context context, String id, SwapiCategory swapiCategory, LoaderManager loaderManager, int loaderId, final StarWarsApiCallback<AllQueryData> apiCallback) {

        ApolloLoader.load(context, loaderManager, loaderId, getApolloCallForItemOnCategoryById(id, swapiCategory), new ApolloCall.Callback() {
            @Override
            public void onResponse(@Nonnull Response response) {
                AllQueryData responseData = new AllQueryData(response, context);
                if (responseData.getCategory() == null) // Responce.Data.Model was null
                    responseData = null;

                apiCallback.onResponse(responseData);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Timber.e(e);
                apiCallback.onResponse(null);
            }
        });
    }

    private ApolloCall getApolloCallForItemOnCategoryById(String id, SwapiCategory swapiCategory) {
        switch (swapiCategory) {
            case FILM:
                return apolloClient.query(FilmQuery.builder().id(id).build());
            case PEOPLE:
                return apolloClient.query(PersonQuery.builder().id(id).build());
            case PLANET:
                return apolloClient.query(PlanetQuery.builder().id(id).build());
            case SPECIES:
                return apolloClient.query(SpeciesQuery.builder().id(id).build());
            case VEHICLE:
                return apolloClient.query(VehicleQuery.builder().id(id).build());
            case STARSHIP:
                return apolloClient.query(StarshipQuery.builder().id(id).build());
        }

        return null;
    }

    private ApolloCall getApolloCallForCategory(SwapiCategory swapiCategory) {
        switch (swapiCategory) {
            case FILM:
                return apolloClient.query(AllFilmsQuery.builder().build());
            case PEOPLE:
                return apolloClient.query(AllPersonsQuery.builder().build());
            case PLANET:
                return apolloClient.query(AllPlanetsQuery.builder().build());
            case SPECIES:
                return apolloClient.query(AllSpeciesQuery.builder().build());
            case VEHICLE:
                return apolloClient.query(AllVehiclesQuery.builder().build());
            case STARSHIP:
                return apolloClient.query(AllStarshipsQuery.builder().build());
        }

        return null;
    }

    private OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), provideX509TrustManager());

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception ex) {
                Timber.e(ex, "Error while setting TLS 1.2");
            }
        }

        return client;
    }

    private X509TrustManager provideX509TrustManager() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            TrustManager[] trustManagers = factory.getTrustManagers();
            return (X509TrustManager) trustManagers[0];
        } catch (NoSuchAlgorithmException | KeyStoreException exception) {
            Timber.e(exception, "Not trust manager available");
        }

        return null;
    }

}

