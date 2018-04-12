package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Species implements Parcelable {
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
    public List<String> getFilms() {
        return films;
    }

    public String getSkinColors() {
        return skinColors;
    }

    public String getHomeworld() {
        return homeworld;
    }

    public String getEdited() {
        return edited;
    }

    public String getCreated() {
        return created;
    }

    public String getEyeColors() {
        return eyeColors;
    }

    public String getLanguage() {
        return language;
    }

    public String getClassification() {
        return classification;
    }

    public List<String> getPeople() {
        return people;
    }

    public String getUrl() {
        return url;
    }

    public String getHairColors() {
        return hairColors;
    }

    public String getAverageHeight() {
        return averageHeight;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getAverageLifespan() {
        return averageLifespan;
    }
    //endregion
}