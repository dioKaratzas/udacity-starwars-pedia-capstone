package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.loaders.RelatedItemsLoader;

public class Film extends SwapiModel {
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
        super();
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

    @Override
    public int getId() {
        return getIdFromUrl(url);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public SwapiCategory getCategory() {
        return SwapiCategory.FILM;
    }

    @Override
    public Map<String, String> getDetailsToDisplay(Context context) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(context.getString(R.string.release_date), releaseDate);
        result.put(context.getString(R.string.director), director);
        result.put(context.getString(R.string.producer), producer);
        result.put(context.getString(R.string.opening_crawl), openingCrawl);

        return result;
    }

    @Override
    public void getRelatedToItemsAsyncLoader(Context context, LoaderManager manager, int loaderId, @NonNull StarWarsApiCallback<Map<String, List<SwapiModel>>> callback) {
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null!");

        Map<String, Map<SwapiCategory, List<String>>> itemsToLoad = new LinkedHashMap<>();
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.PEOPLE, characters);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.planets)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.PLANET, planets);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.species)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.SPECIES, species);
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
        return "Film{" +
                "edited='" + edited + '\'' +
                ", director='" + director + '\'' +
                ", created='" + created + '\'' +
                ", vehicles=" + vehicles +
                ", openingCrawl='" + openingCrawl + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", characters=" + characters +
                ", episodeId=" + episodeId +
                ", planets=" + planets +
                ", releaseDate='" + releaseDate + '\'' +
                ", starships=" + starships +
                ", species=" + species +
                ", producer='" + producer + '\'' +
                '}';
    }

    //endregion
}