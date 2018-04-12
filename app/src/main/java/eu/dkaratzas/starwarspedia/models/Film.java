package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Film implements Parcelable {
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("director")
    private String director;
    @JsonProperty("created")
    private String created;
    @JsonProperty("vehicles")
    private List<String> vehicles;
    @JsonProperty("opening_crawl")
    private String openingCrawl;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("characters")
    private List<String> characters;
    @JsonProperty("episode_id")
    private int episodeId;
    @JsonProperty("planets")
    private List<String> planets;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("starships")
    private List<String> starships;
    @JsonProperty("species")
    private List<String> species;
    @JsonProperty("producer")
    private String producer;

    public Film() {
        this.edited = "";
        this.director = "";
        this.created = "";
        this.vehicles = new ArrayList<>();
        this.openingCrawl = "";
        this.title = "";
        this.url = "";
        this.characters = new ArrayList<>();
        this.episodeId = 0;
        this.planets = new ArrayList<>();
        this.releaseDate = "";
        this.starships = new ArrayList<>();
        this.species = new ArrayList<>();
        this.producer = "";
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.edited);
        dest.writeString(this.director);
        dest.writeString(this.created);
        dest.writeStringList(this.vehicles);
        dest.writeString(this.openingCrawl);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeStringList(this.characters);
        dest.writeInt(this.episodeId);
        dest.writeStringList(this.planets);
        dest.writeString(this.releaseDate);
        dest.writeStringList(this.starships);
        dest.writeStringList(this.species);
        dest.writeString(this.producer);
    }

    protected Film(Parcel in) {
        this.edited = in.readString();
        this.director = in.readString();
        this.created = in.readString();
        this.vehicles = in.createStringArrayList();
        this.openingCrawl = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.characters = in.createStringArrayList();
        this.episodeId = in.readInt();
        this.planets = in.createStringArrayList();
        this.releaseDate = in.readString();
        this.starships = in.createStringArrayList();
        this.species = in.createStringArrayList();
        this.producer = in.readString();
    }

    public static final Parcelable.Creator<Film> CREATOR = new Parcelable.Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel source) {
            return new Film(source);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };
    //endregion

    //region Getters
    public String getEdited() {
        return edited;
    }

    public String getDirector() {
        return director;
    }

    public String getCreated() {
        return created;
    }

    public List<String> getVehicles() {
        return vehicles;
    }

    public String getOpeningCrawl() {
        return openingCrawl;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public List<String> getPlanets() {
        return planets;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<String> getStarships() {
        return starships;
    }

    public List<String> getSpecies() {
        return species;
    }

    public String getProducer() {
        return producer;
    }
    //endregion
}