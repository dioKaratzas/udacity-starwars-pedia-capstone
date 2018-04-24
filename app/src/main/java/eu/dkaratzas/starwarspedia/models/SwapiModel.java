package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import timber.log.Timber;

public abstract class SwapiModel implements Parcelable {

    public abstract int getId();

    public abstract String getTitle();

    public abstract SwapiCategory getCategory();

    /**
     * Its responsible to call RelatedItemsLoader and provide the required List of items to load
     * The loader returns a Map with key the Title to display and values a List[SwapiModel]
     */
    public abstract void getRelatedToItemsAsyncLoader(Context context, LoaderManager manager, int loaderId, @NonNull StarWarsApiCallback<Map<String, List<SwapiModel>>> callback);

    public abstract Map<String, String> getDetailsToDisplay(final Context context);

    public StorageReference getImageStorageReference() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        switch (getCategory()) {
            case FILM:
                return storageRef.child("films/" + getId() + ".jpg");
            case PEOPLE:
                return storageRef.child("people/" + getId() + ".jpg");
            case PLANET:
                return storageRef.child("planets/" + getId() + ".jpg");
            case SPECIES:
                return storageRef.child("species/" + getId() + ".jpg");
            case STARSHIP:
                return storageRef.child("starships/" + getId() + ".jpg");
            case VEHICLE:
                return storageRef.child("vehicles/" + getId() + ".jpg");
        }
        return null;
    }

    public static int getIdFromUrl(String url) {
        if (url != null && !url.equals("")) {
            final String regex = "https.*/([0-9]+)/(?:&.)*";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(url);


            if (matcher.matches()) {
                int result = Integer.parseInt(matcher.group(1));

                Timber.v("Url: %s, Id: %d", url, result);
                return result;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "SwapiModel{}";
    }

}
