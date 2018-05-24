package eu.dkaratzas.starwarspedia.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import eu.dkaratzas.starwarspedia.api.SwapiCategory;

public class SimpleQueryData implements Parcelable {
    private String id;
    private String title;
    private SwapiCategory category;
    private Bitmap image;

    public SimpleQueryData(String id, String title, SwapiCategory category) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.image = null;
    }

    public SimpleQueryData(String id, String title, SwapiCategory category, Bitmap image) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public SwapiCategory getCategory() {
        return category;
    }

    public StorageReference getImageStorageReference() {
        String imageName = getTitle().replace('/', '_');

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        switch (category) {
            case FILM:
                return storageRef.child("films/" + imageName + ".jpg");
            case PEOPLE:
                return storageRef.child("people/" + imageName + ".jpg");
            case PLANET:
                return storageRef.child("planets/" + imageName + ".jpg");
            case SPECIES:
                return storageRef.child("species/" + imageName + ".jpg");
            case STARSHIP:
                return storageRef.child("starships/" + imageName + ".jpg");
            case VEHICLE:
                return storageRef.child("vehicles/" + imageName + ".jpg");
        }
        return null;
    }

    public Bitmap getImage() {
        return image;
    }

    // region Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
    }

    protected SimpleQueryData(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : SwapiCategory.values()[tmpCategory];
    }

    public static final Parcelable.Creator<SimpleQueryData> CREATOR = new Parcelable.Creator<SimpleQueryData>() {
        @Override
        public SimpleQueryData createFromParcel(Parcel source) {
            return new SimpleQueryData(source);
        }

        @Override
        public SimpleQueryData[] newArray(int size) {
            return new SimpleQueryData[size];
        }
    };

    // endregion
}