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

public class Species extends SwapiModel {
    @JsonProperty("films")
    private List<String> films;
    @JsonProperty("skin_colors")
    private String skinColors;
    @JsonProperty("homeworld")
    private String homeworld;
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("created")
    private String created;
    @JsonProperty("eye_colors")
    private String eyeColors;
    @JsonProperty("language")
    private String language;
    @JsonProperty("classification")
    private String classification;
    @JsonProperty("people")
    private List<String> people;
    @JsonProperty("url")
    private String url;
    @JsonProperty("hair_colors")
    private String hairColors;
    @JsonProperty("average_height")
    private String averageHeight;
    @JsonProperty("name")
    private String name;
    @JsonProperty("designation")
    private String designation;
    @JsonProperty("average_lifespan")
    private String averageLifespan;

    public Species() {
        super();
        this.films = new ArrayList<>();
        this.skinColors = "";
        this.homeworld = "";
        this.edited = "";
        this.created = "";
        this.eyeColors = "";
        this.language = "";
        this.classification = "";
        this.people = new ArrayList<>();
        this.url = "";
        this.hairColors = "";
        this.averageHeight = "";
        this.name = "";
        this.designation = "";
        this.averageLifespan = "";
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.films);
        dest.writeString(this.skinColors);
        dest.writeString(this.homeworld);
        dest.writeString(this.edited);
        dest.writeString(this.created);
        dest.writeString(this.eyeColors);
        dest.writeString(this.language);
        dest.writeString(this.classification);
        dest.writeStringList(this.people);
        dest.writeString(this.url);
        dest.writeString(this.hairColors);
        dest.writeString(this.averageHeight);
        dest.writeString(this.name);
        dest.writeString(this.designation);
        dest.writeString(this.averageLifespan);
    }

    protected Species(Parcel in) {
        this.films = in.createStringArrayList();
        this.skinColors = in.readString();
        this.homeworld = in.readString();
        this.edited = in.readString();
        this.created = in.readString();
        this.eyeColors = in.readString();
        this.language = in.readString();
        this.classification = in.readString();
        this.people = in.createStringArrayList();
        this.url = in.readString();
        this.hairColors = in.readString();
        this.averageHeight = in.readString();
        this.name = in.readString();
        this.designation = in.readString();
        this.averageLifespan = in.readString();
    }

    public static final Parcelable.Creator<Species> CREATOR = new Parcelable.Creator<Species>() {
        @Override
        public Species createFromParcel(Parcel source) {
            return new Species(source);
        }

        @Override
        public Species[] newArray(int size) {
            return new Species[size];
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
        return SwapiCategory.SPECIES;
    }

    @Override
    public Map<String, String> getDetailsToDisplay(Context context) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(context.getString(R.string.classification), classification);
        result.put(context.getString(R.string.designation), designation);
        result.put(context.getString(R.string.language), language);
        result.put(context.getString(R.string.avg_lifespan), averageLifespan);
        result.put(context.getString(R.string.avg_height), averageHeight);
        result.put(context.getString(R.string.hair_color), hairColors);
        result.put(context.getString(R.string.skin_color), skinColors);
        result.put(context.getString(R.string.eye_color), eyeColors);

        return result;
    }

    @Override
    public void getRelatedToItemsAsyncLoader(Context context, LoaderManager manager, int loaderId, @NonNull StarWarsApiCallback<Map<String, List<SwapiModel>>> callback) {
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null!");

        Map<String, Map<SwapiCategory, List<String>>> itemsToLoad = new LinkedHashMap<>();
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.FILM, films);
        }});
        itemsToLoad.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), new LinkedHashMap<SwapiCategory, List<String>>() {{
            put(SwapiCategory.PEOPLE, people);
        }});


        RelatedItemsLoader.load(context, manager, loaderId, itemsToLoad, callback);
    }


    @Override
    public String toString() {
        return "Species{" +
                "films=" + films +
                ", skinColors='" + skinColors + '\'' +
                ", homeworld='" + homeworld + '\'' +
                ", edited='" + edited + '\'' +
                ", created='" + created + '\'' +
                ", eyeColors='" + eyeColors + '\'' +
                ", language='" + language + '\'' +
                ", classification='" + classification + '\'' +
                ", people=" + people +
                ", url='" + url + '\'' +
                ", hairColors='" + hairColors + '\'' +
                ", averageHeight='" + averageHeight + '\'' +
                ", name='" + name + '\'' +
                ", designation='" + designation + '\'' +
                ", averageLifespan='" + averageLifespan + '\'' +
                '}';
    }

    //endregion
}