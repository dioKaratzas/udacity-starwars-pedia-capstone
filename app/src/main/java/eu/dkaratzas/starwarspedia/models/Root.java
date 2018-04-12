package eu.dkaratzas.starwarspedia.models;

/**
 * Created by JacksonGenerator on 4/12/18.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Root implements Parcelable {
    @JsonProperty("films")
    private String films;
    @JsonProperty("planets")
    private String planets;
    @JsonProperty("species")
    private String species;
    @JsonProperty("starships")
    private String starships;
    @JsonProperty("vehicles")
    private String vehicles;
    @JsonProperty("people")
    private String people;

    public Root() {
        this.films = "";
        this.planets = "";
        this.species = "";
        this.starships = "";
        this.vehicles = "";
        this.people = "";
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.films);
        dest.writeString(this.planets);
        dest.writeString(this.species);
        dest.writeString(this.starships);
        dest.writeString(this.vehicles);
        dest.writeString(this.people);
    }

    protected Root(Parcel in) {
        this.films = in.readString();
        this.planets = in.readString();
        this.species = in.readString();
        this.starships = in.readString();
        this.vehicles = in.readString();
        this.people = in.readString();
    }

    public static final Parcelable.Creator<Root> CREATOR = new Parcelable.Creator<Root>() {
        @Override
        public Root createFromParcel(Parcel source) {
            return new Root(source);
        }

        @Override
        public Root[] newArray(int size) {
            return new Root[size];
        }
    };
    //endregion

    //region Getters
    public String getFilms() {
        return films;
    }

    public String getPlanets() {
        return planets;
    }

    public String getSpecies() {
        return species;
    }

    public String getStarships() {
        return starships;
    }

    public String getVehicles() {
        return vehicles;
    }

    public String getPeople() {
        return people;
    }
    //endregion
}