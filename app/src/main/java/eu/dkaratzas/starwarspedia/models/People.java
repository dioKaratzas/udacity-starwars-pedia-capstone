package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;

public class People extends SwapiModel {
    private List<String> films;
    private String homeworld;
    private String gender;
    private String skinColor;
    private String edited;
    private String created;
    private String mass;
    private List<String> vehicles;
    private String url;
    private String hairColor;
    private String birthYear;
    private String eyeColor;
    private List<String> species;
    private List<String> starships;
    private String name;
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

    @JsonCreator
    public People(@JsonProperty("films") List<String> films,
                  @JsonProperty("homeworld") String homeworld,
                  @JsonProperty("gender") String gender,
                  @JsonProperty("skin_color") String skinColor,
                  @JsonProperty("edited") String edited,
                  @JsonProperty("created") String created,
                  @JsonProperty("mass") String mass,
                  @JsonProperty("vehicles") List<String> vehicles,
                  @JsonProperty("url") String url,
                  @JsonProperty("hair_color") String hairColor,
                  @JsonProperty("birth_year") String birthYear,
                  @JsonProperty("eye_color") String eyeColor,
                  @JsonProperty("species") List<String> species,
                  @JsonProperty("starships") List<String> starships,
                  @JsonProperty("name") String name,
                  @JsonProperty("height") String height) {
        this.films = films;
        this.homeworld = homeworld;
        this.gender = gender;
        this.skinColor = skinColor;
        this.edited = edited;
        this.created = created;
        this.mass = mass;
        this.vehicles = vehicles;
        this.url = url;
        this.hairColor = hairColor;
        this.birthYear = birthYear;
        this.eyeColor = eyeColor;
        this.species = species;
        this.starships = starships;
        this.name = name;
        this.height = height;
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
    public List<String> getFilms() {
        return films;
    }

    public String getHomeworld() {
        return homeworld;
    }

    public String getGender() {
        return gender;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public String getEdited() {
        return edited;
    }

    public String getCreated() {
        return created;
    }

    public String getMass() {
        return mass;
    }

    public List<String> getVehicles() {
        return vehicles;
    }

    public String getUrl() {
        return url;
    }

    public String getHairColor() {
        return hairColor;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public List<String> getSpecies() {
        return species;
    }

    public List<String> getStarships() {
        return starships;
    }

    public String getName() {
        return name;
    }

    public String getHeight() {
        return height;
    }

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
    public void getDetailsToDisplay(@NonNull final StarWarsApiCallback<Map<String, Object>> callback, Context context) {
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null!");

        final Map<String, Object> result = new HashMap<>();
        result.put("Birth Year", birthYear);
        result.put("Height", height);
        result.put("Mass", mass);
        result.put("Gender", gender);
        result.put("Hair Color", hairColor);
        result.put("Skin Color", skinColor);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unused) {
                if (species != null && species.size() > 0) {
                    SwapiModel swapiModel = (SwapiModel) StarWarsApi.getApi().getItemRequestOnCategoryById(getIdFromUrl(species.get(0)), SwapiCategory.SPECIES).sync();
                    result.put("Species", swapiModel);
                }
                if (homeworld != null && !homeworld.equals("")) {
                    SwapiModel swapiModel = (SwapiModel) StarWarsApi.getApi().getItemRequestOnCategoryById(getIdFromUrl(homeworld), SwapiCategory.PLANET).sync();
                    result.put("Homeworld", swapiModel);
                }
                callback.onResponse(result);
                return null;
            }
        }.execute();
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