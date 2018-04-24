package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.loaders.RelatedItemsLoader;

public class People extends SwapiModel {
    @JsonProperty("films")
    private List<String> films;
    @JsonProperty("homeworld")
    private String homeworld;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("skin_color")
    private String skinColor;
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("created")
    private String created;
    @JsonProperty("mass")
    private String mass;
    @JsonProperty("vehicles")
    private List<String> vehicles;
    @JsonProperty("url")
    private String url;
    @JsonProperty("hair_color")
    private String hairColor;
    @JsonProperty("birth_year")
    private String birthYear;
    @JsonProperty("eye_color")
    private String eyeColor;
    @JsonProperty("species")
    private List<String> species;
    @JsonProperty("starships")
    private List<String> starships;
    @JsonProperty("name")
    private String name;
    @JsonProperty("height")
    private String height;

    public People() {
        super();
        this.films = new ArrayList<>();
        this.homeworld = "";
        this.gender = "";
        this.skinColor = "";
        this.edited = "";
        this.created = "";
        this.mass = "";
        this.vehicles = new ArrayList<>();
        this.url = "";
        this.hairColor = "";
        this.birthYear = "";
        this.eyeColor = "";
        this.species = new ArrayList<>();
        this.starships = new ArrayList<>();
        this.name = "";
        this.height = "";
    }

    //region Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.films);
        dest.writeString(this.homeworld);
        dest.writeString(this.gender);
        dest.writeString(this.skinColor);
        dest.writeString(this.edited);
        dest.writeString(this.created);
        dest.writeString(this.mass);
        dest.writeStringList(this.vehicles);
        dest.writeString(this.url);
        dest.writeString(this.hairColor);
        dest.writeString(this.birthYear);
        dest.writeString(this.eyeColor);
        dest.writeStringList(this.species);
        dest.writeStringList(this.starships);
        dest.writeString(this.name);
        dest.writeString(this.height);
    }

    protected People(Parcel in) {
        this.films = in.createStringArrayList();
        this.homeworld = in.readString();
        this.gender = in.readString();
        this.skinColor = in.readString();
        this.edited = in.readString();
        this.created = in.readString();
        this.mass = in.readString();
        this.vehicles = in.createStringArrayList();
        this.url = in.readString();
        this.hairColor = in.readString();
        this.birthYear = in.readString();
        this.eyeColor = in.readString();
        this.species = in.createStringArrayList();
        this.starships = in.createStringArrayList();
        this.name = in.readString();
        this.height = in.readString();
    }

    public static final Parcelable.Creator<People> CREATOR = new Parcelable.Creator<People>() {
        @Override
        public People createFromParcel(Parcel source) {
            return new People(source);
        }

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }
    };
    //endregion

    //region Getters
    @Override
    public int getId() {
        return getIdFromUrl(url);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public SwapiCategory getCategory() {
        return SwapiCategory.PEOPLE;
    }

    @Override
    public Map<String, String> getDetailsToDisplay(final Context context) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(context.getString(R.string.birth_year), birthYear);
        result.put(context.getString(R.string.height), height);
        result.put(context.getString(R.string.mass), mass);
        result.put(context.getString(R.string.gender), gender);
        result.put(context.getString(R.string.hair_color), hairColor);
        result.put(context.getString(R.string.skin_color), skinColor);

        return result;
    }


    @Override
    public void getRelatedToItemsAsyncLoader(Context context, LoaderManager manager, int loaderId, @NonNull StarWarsApiCallback<Map<String, List<SwapiModel>>> callback) {
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null!");

        Map<String, Map<SwapiCategory, List<String>>> itemsToLoad = new LinkedHashMap<>();
        itemsToLoad.put(context.getString(R.string.homeworld), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.PLANET, Arrays.asList(homeworld));
        }});
        itemsToLoad.put(context.getString(R.string.species), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.SPECIES, species);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.FILM, films);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.starships)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.STARSHIP, starships);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.vehicles)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.VEHICLE, vehicles);
        }});

        RelatedItemsLoader.load(context, manager, loaderId, itemsToLoad, callback);
    }


    @Override
    public String toString() {
        return "People{" +
                "films=" + films +
                ", homeworld='" + homeworld + '\'' +
                ", gender='" + gender + '\'' +
                ", skinColor='" + skinColor + '\'' +
                ", edited='" + edited + '\'' +
                ", created='" + created + '\'' +
                ", mass='" + mass + '\'' +
                ", vehicles=" + vehicles +
                ", url='" + url + '\'' +
                ", hairColor='" + hairColor + '\'' +
                ", birthYear='" + birthYear + '\'' +
                ", eyeColor='" + eyeColor + '\'' +
                ", species=" + species +
                ", starships=" + starships +
                ", name='" + name + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
    //endregion
}