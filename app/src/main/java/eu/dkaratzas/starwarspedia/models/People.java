package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class People implements Parcelable {
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