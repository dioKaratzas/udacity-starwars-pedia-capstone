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

public class Planet extends SwapiModel {
    @JsonProperty("films")
    private List<String> films;
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("created")
    private String created;
    @JsonProperty("climate")
    private String climate;
    @JsonProperty("rotation_period")
    private String rotationPeriod;
    @JsonProperty("url")
    private String url;
    @JsonProperty("population")
    private String population;
    @JsonProperty("orbital_period")
    private String orbitalPeriod;
    @JsonProperty("surface_water")
    private String surfaceWater;
    @JsonProperty("diameter")
    private String diameter;
    @JsonProperty("gravity")
    private String gravity;
    @JsonProperty("name")
    private String name;
    @JsonProperty("residents")
    private List<String> residents;
    @JsonProperty("terrain")
    private String terrain;

    public Planet() {
        super();
        this.films = new ArrayList<>();
        this.edited = "";
        this.created = "";
        this.climate = "";
        this.rotationPeriod = "";
        this.url = "";
        this.population = "";
        this.orbitalPeriod = "";
        this.surfaceWater = "";
        this.diameter = "";
        this.gravity = "";
        this.name = "";
        this.residents = new ArrayList<>();
        this.terrain = "";
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.films);
        dest.writeString(this.edited);
        dest.writeString(this.created);
        dest.writeString(this.climate);
        dest.writeString(this.rotationPeriod);
        dest.writeString(this.url);
        dest.writeString(this.population);
        dest.writeString(this.orbitalPeriod);
        dest.writeString(this.surfaceWater);
        dest.writeString(this.diameter);
        dest.writeString(this.gravity);
        dest.writeString(this.name);
        dest.writeStringList(this.residents);
        dest.writeString(this.terrain);
    }

    protected Planet(Parcel in) {
        this.films = in.createStringArrayList();
        this.edited = in.readString();
        this.created = in.readString();
        this.climate = in.readString();
        this.rotationPeriod = in.readString();
        this.url = in.readString();
        this.population = in.readString();
        this.orbitalPeriod = in.readString();
        this.surfaceWater = in.readString();
        this.diameter = in.readString();
        this.gravity = in.readString();
        this.name = in.readString();
        this.residents = in.createStringArrayList();
        this.terrain = in.readString();
    }

    public static final Parcelable.Creator<Planet> CREATOR = new Parcelable.Creator<Planet>() {
        @Override
        public Planet createFromParcel(Parcel source) {
            return new Planet(source);
        }

        @Override
        public Planet[] newArray(int size) {
            return new Planet[size];
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
        return SwapiCategory.PLANET;
    }

    @Override
    public Map<String, String> getDetailsToDisplay(Context context) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(context.getString(R.string.population), population);
        result.put(context.getString(R.string.rotation_period), rotationPeriod);
        result.put(context.getString(R.string.orbital_period), orbitalPeriod);
        result.put(context.getString(R.string.diameter), diameter);
        result.put(context.getString(R.string.gravity), gravity);
        result.put(context.getString(R.string.terrain), terrain);
        result.put(context.getString(R.string.surface_water), surfaceWater);
        result.put(context.getString(R.string.climate), climate);

        return result;
    }

    @Override
    public void getRelatedToItemsAsyncLoader(Context context, LoaderManager manager, int loaderId, @NonNull StarWarsApiCallback<Map<String, List<SwapiModel>>> callback) {
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null!");

        // Title, [SwapiCategory, List[Urls]]
        Map<String, Map<SwapiCategory, List<String>>> itemsToLoad = new LinkedHashMap<>();
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.FILM, films);
        }});
        itemsToLoad.put(context.getString(R.string.residents), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.PLANET, residents);
        }});

        RelatedItemsLoader.load(context, manager, loaderId, itemsToLoad, callback);
    }

    @Override
    public String toString() {
        return "Planet{" +
                "films=" + films +
                ", edited='" + edited + '\'' +
                ", created='" + created + '\'' +
                ", climate='" + climate + '\'' +
                ", rotationPeriod='" + rotationPeriod + '\'' +
                ", url='" + url + '\'' +
                ", population='" + population + '\'' +
                ", orbitalPeriod='" + orbitalPeriod + '\'' +
                ", surfaceWater='" + surfaceWater + '\'' +
                ", diameter='" + diameter + '\'' +
                ", gravity='" + gravity + '\'' +
                ", name='" + name + '\'' +
                ", residents=" + residents +
                ", terrain='" + terrain + '\'' +
                '}';
    }

    //endregion
}