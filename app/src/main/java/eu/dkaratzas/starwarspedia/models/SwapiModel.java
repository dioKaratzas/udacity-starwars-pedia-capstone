package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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
    protected Map<String, Object> detailsMapList;
    protected Map<SwapiCategory, List<SwapiModel>> relatedMapList;

    public abstract int getId();

    public abstract String getTitle();

    public abstract SwapiCategory getCategory();

    public void getDetailsToDisplay(@NonNull final StarWarsApiCallback<Map<String, Object>> callback, Context context) {
    }

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

    int getIdFromUrl(String url) {
        final String regex = "https.*/([0-9]+)/(?:&.)*";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);


        if (matcher.matches()) {
            int result = Integer.parseInt(matcher.group(1));

            Timber.v("Category: %s, Title: %s, Id: %d", getCategory().toString(), getTitle(), result);
            return result;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "SwapiModel{}";
    }

    public Map<String, Object> getDetailsMapList() {
        return detailsMapList;
    }

    public Map<SwapiCategory, List<SwapiModel>> getRelatedMapList() {
        return relatedMapList;
    }
}
