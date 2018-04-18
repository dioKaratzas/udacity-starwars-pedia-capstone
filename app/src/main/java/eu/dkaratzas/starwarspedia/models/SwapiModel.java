package eu.dkaratzas.starwarspedia.models;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.dkaratzas.starwarspedia.api.SwapiCategory;

public abstract class SwapiModel {

    public abstract int getId();

    public abstract String getTitle();

    public abstract SwapiCategory getCategory();

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

    public int getIdFromUrl(String url) {
        final String regex = "https.*/([0-9]+)/(?:&.)*";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }
}
