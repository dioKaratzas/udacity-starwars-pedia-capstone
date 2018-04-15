package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eu.dkaratzas.starwarspedia.api.SwapiCategory;

public class Starship extends SwapiModel implements Parcelable {
    @JsonProperty("max_atmosphering_speed")
    private String maxAtmospheringSpeed;
    @JsonProperty("cargo_capacity")
    private String cargoCapacity;
    @JsonProperty("films")
    private List<String> films;
    @JsonProperty("passengers")
    private String passengers;
    @JsonProperty("pilots")
    private List<String> pilots;
    @JsonProperty("edited")
    private String edited;
    @JsonProperty("consumables")
    private String consumables;
    @JsonProperty("MGLT")
    private String mGLT;
    @JsonProperty("created")
    private String created;
    @JsonProperty("length")
    private String length;
    @JsonProperty("starship_class")
    private String starshipClass;
    @JsonProperty("url")
    private String url;
    @JsonProperty("manufacturer")
    private String manufacturer;
    @JsonProperty("crew")
    private String crew;
    @JsonProperty("hyperdrive_rating")
    private String hyperdriveRating;
    @JsonProperty("cost_in_credits")
    private String costInCredits;
    @JsonProperty("name")
    private String name;
    @JsonProperty("model")
    private String model;

    public Starship() {
        this.maxAtmospheringSpeed = "";
        this.cargoCapacity = "";
        this.films = new ArrayList<>();
        this.passengers = "";
        this.pilots = new ArrayList();
        this.edited = "";
        this.consumables = "";
        this.mGLT = "";
        this.created = "";
        this.length = "";
        this.starshipClass = "";
        this.url = "";
        this.manufacturer = "";
        this.crew = "";
        this.hyperdriveRating = "";
        this.costInCredits = "";
        this.name = "";
        this.model = "";
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.maxAtmospheringSpeed);
        dest.writeString(this.cargoCapacity);
        dest.writeStringList(this.films);
        dest.writeString(this.passengers);
        dest.writeStringList(this.pilots);
        dest.writeString(this.edited);
        dest.writeString(this.consumables);
        dest.writeString(this.mGLT);
        dest.writeString(this.created);
        dest.writeString(this.length);
        dest.writeString(this.starshipClass);
        dest.writeString(this.url);
        dest.writeString(this.manufacturer);
        dest.writeString(this.crew);
        dest.writeString(this.hyperdriveRating);
        dest.writeString(this.costInCredits);
        dest.writeString(this.name);
        dest.writeString(this.model);
    }

    protected Starship(Parcel in) {
        this.maxAtmospheringSpeed = in.readString();
        this.cargoCapacity = in.readString();
        this.films = in.createStringArrayList();
        this.passengers = in.readString();
        this.pilots = in.createStringArrayList();
        this.edited = in.readString();
        this.consumables = in.readString();
        this.mGLT = in.readString();
        this.created = in.readString();
        this.length = in.readString();
        this.starshipClass = in.readString();
        this.url = in.readString();
        this.manufacturer = in.readString();
        this.crew = in.readString();
        this.hyperdriveRating = in.readString();
        this.costInCredits = in.readString();
        this.name = in.readString();
        this.model = in.readString();
    }

    public static final Parcelable.Creator<Starship> CREATOR = new Parcelable.Creator<Starship>() {
        @Override
        public Starship createFromParcel(Parcel source) {
            return new Starship(source);
        }

        @Override
        public Starship[] newArray(int size) {
            return new Starship[size];
        }
    };
    //endregion

    //region Getter

    public String getMaxAtmospheringSpeed() {
        return maxAtmospheringSpeed;
    }

    public String getCargoCapacity() {
        return cargoCapacity;
    }

    public List<String> getFilms() {
        return films;
    }

    public String getPassengers() {
        return passengers;
    }

    public List<String> getPilots() {
        return pilots;
    }

    public String getEdited() {
        return edited;
    }

    public String getConsumables() {
        return consumables;
    }

    public String getmGLT() {
        return mGLT;
    }

    public String getCreated() {
        return created;
    }

    public String getLength() {
        return length;
    }

    public String getStarshipClass() {
        return starshipClass;
    }

    public String getUrl() {
        return url;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getCrew() {
        return crew;
    }

    public String getHyperdriveRating() {
        return hyperdriveRating;
    }

    public String getCostInCredits() {
        return costInCredits;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
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
        return SwapiCategory.STARSHIP;
    }
    //endregion
}