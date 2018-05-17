package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.apollographql.apollo.api.Response;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import api.FilmQuery;
import api.PersonQuery;
import api.PlanetQuery;
import api.SpeciesQuery;
import api.StarshipQuery;
import api.VehicleQuery;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import timber.log.Timber;

/**
 * Mapping apollo result for the ItemDataById GraphQL Responses
 */
public class AllQueryData implements Parcelable {
    private String id;
    private String title;
    private SwapiCategory category;
    private LinkedHashMap<String, String> detailsMap;
    private LinkedHashMap<String, List<SimpleQueryData>> relatedItems;

    public AllQueryData(Response response, Context context) {

        if (response.data() != null) {

            if (response.data() instanceof FilmQuery.Data) {
                mapFilmData(context, (FilmQuery.Data) response.data());
            } else if (response.data() instanceof PersonQuery.Data) {
                mapPersonData(context, (PersonQuery.Data) response.data());
            } else if (response.data() instanceof PlanetQuery.Data) {
                mapPlanetData(context, (PlanetQuery.Data) response.data());
            } else if (response.data() instanceof SpeciesQuery.Data) {
                mapSpeciesData(context, (SpeciesQuery.Data) response.data());
            } else if (response.data() instanceof StarshipQuery.Data) {
                mapStarshipsData(context, (StarshipQuery.Data) response.data());
            } else if (response.data() instanceof VehicleQuery.Data) {
                mapVehiclesData(context, (VehicleQuery.Data) response.data());
            } else {
                Timber.d("Unknown response.data instance.");
            }

        } else {
            Timber.d("Response data is null");
        }
    }

    private void mapFilmData(Context context, FilmQuery.Data data) {
        FilmQuery.Film film = data.Film();

        if (film != null) {
            this.id = getSafeString(film.id());
            this.title = getSafeString(film.title());
            this.category = SwapiCategory.FILM;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();


            detailsMap.put(context.getString(R.string.release_date), getSafeString(DateFormat.getDateInstance(DateFormat.LONG).format(film.releaseDate())));
            detailsMap.put(context.getString(R.string.director), getSafeString(film.director()));
            detailsMap.put(context.getString(R.string.producer), getSafeString(film.producers()));
            detailsMap.put(context.getString(R.string.opening_crawl), getSafeString(film.openingCrawl()));

            if (film.characters() != null && film.characters().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (FilmQuery.Character character : film.characters()) {
                    items.add(new SimpleQueryData(character.id(), character.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), items);
            }

            if (film.planets() != null && film.planets().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (FilmQuery.Planet planet : film.planets()) {
                    items.add(new SimpleQueryData(planet.id(), planet.name(), SwapiCategory.PLANET));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.planets)), items);
            }

            if (film.species() != null && film.species().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (FilmQuery.Species species : film.species()) {
                    items.add(new SimpleQueryData(species.id(), species.name(), SwapiCategory.SPECIES));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.species)), items);
            }

            if (film.starships() != null && film.starships().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (FilmQuery.Starship starship : film.starships()) {
                    items.add(new SimpleQueryData(starship.id(), starship.name(), SwapiCategory.STARSHIP));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.starships)), items);
            }

            if (film.vehicles() != null && film.vehicles().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (FilmQuery.Vehicle vehicle : film.vehicles()) {
                    items.add(new SimpleQueryData(vehicle.id(), vehicle.name(), SwapiCategory.VEHICLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.vehicles)), items);
            }
        }
    }

    private void mapPersonData(Context context, PersonQuery.Data data) {
        PersonQuery.Person person = data.Person();

        if (person != null) {
            this.id = getSafeString(person.id());
            this.title = getSafeString(person.name());
            this.category = SwapiCategory.PEOPLE;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.birth_year), getSafeString(person.birthYear()));
            detailsMap.put(context.getString(R.string.height), getSafeString(person.height()));
            detailsMap.put(context.getString(R.string.mass), getSafeString(person.mass()));
            detailsMap.put(context.getString(R.string.gender), getSafeString(person.gender()));

            String hairColor = getListEnumValuesFormatted(person.hairColor());
            if (hairColor != null && !"".equals(hairColor))
                detailsMap.put(context.getString(R.string.hair_color), hairColor);

            String skinColor = getListEnumValuesFormatted(person.skinColor());
            if (skinColor != null && !"".equals(skinColor))
                detailsMap.put(context.getString(R.string.skin_color), skinColor);

            if (person.homeworld() != null) {
                List<SimpleQueryData> items = new ArrayList<>();

                items.add(new SimpleQueryData(person.homeworld().id(), person.homeworld().name(), SwapiCategory.PLANET));

                relatedItems.put(context.getString(R.string.homeworld), items);
            }

            if (person.films() != null && person.films().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PersonQuery.Film film : person.films()) {
                    items.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }

            if (person.species() != null && person.species().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PersonQuery.Species species : person.species()) {
                    items.add(new SimpleQueryData(species.id(), species.name(), SwapiCategory.SPECIES));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.species)), items);
            }

            if (person.starships() != null && person.starships().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PersonQuery.Starship starship : person.starships()) {
                    items.add(new SimpleQueryData(starship.id(), starship.name(), SwapiCategory.STARSHIP));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.starships)), items);
            }

            if (person.vehicles() != null && person.vehicles().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PersonQuery.Vehicle vehicle : person.vehicles()) {
                    items.add(new SimpleQueryData(vehicle.id(), vehicle.name(), SwapiCategory.VEHICLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.vehicles)), items);
            }
        }
    }

    private void mapPlanetData(Context context, PlanetQuery.Data data) {
        PlanetQuery.Planet planet = data.Planet();

        if (planet != null) {
            this.id = getSafeString(planet.id());
            this.title = getSafeString(planet.name());
            this.category = SwapiCategory.PLANET;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.population), getSafeString(planet.population()));
            detailsMap.put(context.getString(R.string.rotation_period), getSafeString(planet.rotationPeriod()));
            detailsMap.put(context.getString(R.string.orbital_period), getSafeString(planet.orbitalPeriod()));
            detailsMap.put(context.getString(R.string.diameter), getSafeString(planet.diameter()));
            detailsMap.put(context.getString(R.string.gravity), getSafeString(planet.gravity()));
            detailsMap.put(context.getString(R.string.terrain), getSafeString(planet.terrain()));
            detailsMap.put(context.getString(R.string.surface_water), getSafeString(planet.surfaceWater()));
            detailsMap.put(context.getString(R.string.climate), getSafeString(planet.climate()));


            if (planet.residents() != null && planet.residents().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PlanetQuery.Resident resident : planet.residents()) {
                    items.add(new SimpleQueryData(resident.id(), resident.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(context.getString(R.string.residents), items);
            }

            if (planet.films() != null && planet.films().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (PlanetQuery.Film film : planet.films()) {
                    items.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }


        }
    }

    private void mapSpeciesData(Context context, SpeciesQuery.Data data) {
        SpeciesQuery.Species species = data.Species();

        if (species != null) {
            this.id = getSafeString(species.id());
            this.title = getSafeString(species.name());
            this.category = SwapiCategory.SPECIES;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.classification), getSafeString(species.classification()));
            detailsMap.put(context.getString(R.string.designation), getSafeString(species.designation()));
            detailsMap.put(context.getString(R.string.language), getSafeString(species.language()));
            detailsMap.put(context.getString(R.string.avg_lifespan), getSafeString(species.averageLifespan()));
            detailsMap.put(context.getString(R.string.avg_height), getSafeString(species.averageHeight()));

            String hairColor = getListEnumValuesFormatted(species.hairColor());
            if (hairColor != null && !"".equals(hairColor))
                detailsMap.put(context.getString(R.string.hair_color), hairColor);

            String skinColor = getListEnumValuesFormatted(species.skinColor());
            if (skinColor != null && !"".equals(skinColor))
                detailsMap.put(context.getString(R.string.skin_color), skinColor);

            String eyeColor = getListEnumValuesFormatted(species.eyeColor());
            if (eyeColor != null && !"".equals(eyeColor))
                detailsMap.put(context.getString(R.string.eye_color), eyeColor);

            if (species.people() != null && species.people().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (SpeciesQuery.person people : species.people()) {
                    items.add(new SimpleQueryData(people.id(), people.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), items);
            }

            if (species.films() != null && species.films().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (SpeciesQuery.Film film : species.films()) {
                    items.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }
        }
    }

    private void mapStarshipsData(Context context, StarshipQuery.Data data) {
        StarshipQuery.Starship starship = data.Starship();

        if (starship != null) {
            this.id = getSafeString(starship.id());
            this.title = getSafeString(starship.name());
            this.category = SwapiCategory.STARSHIP;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.manufacturer), getSafeString(starship.manufacturer()));
            detailsMap.put(context.getString(R.string.starship_class), getSafeString(starship.class_()));
            detailsMap.put(context.getString(R.string.cost), getSafeString(starship.costInCredits()));
            detailsMap.put(context.getString(R.string.speed), getSafeString(starship.maxAtmospheringSpeed()));
            detailsMap.put(context.getString(R.string.hyperdrive_rating), getSafeString(starship.hyperdriveRating()));
            detailsMap.put(context.getString(R.string.mglt), getSafeString(starship.mglt()));
            detailsMap.put(context.getString(R.string.length), getSafeString(starship.length()));
            detailsMap.put(context.getString(R.string.cargo_capacity), getSafeString(starship.cargoCapacity()));
            detailsMap.put(context.getString(R.string.crew), getSafeString(starship.crew()));
            detailsMap.put(context.getString(R.string.passengers), getSafeString(starship.passengers()));
            detailsMap.put(context.getString(R.string.consumables), getSafeString(starship.consumables()));

            if (starship.pilots() != null && starship.pilots().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (StarshipQuery.Pilot pilot : starship.pilots()) {
                    items.add(new SimpleQueryData(pilot.id(), pilot.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.pilots)), items);
            }

            if (starship.films() != null && starship.films().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (StarshipQuery.Film film : starship.films()) {
                    items.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }
        }
    }

    private void mapVehiclesData(Context context, VehicleQuery.Data data) {
        VehicleQuery.Vehicle vehicle = data.Vehicle();

        if (vehicle != null) {
            this.id = getSafeString(vehicle.id());
            this.title = getSafeString(vehicle.name());
            this.category = SwapiCategory.STARSHIP;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.manufacturer), getSafeString(vehicle.manufacturer()));
            detailsMap.put(context.getString(R.string.model), getSafeString(vehicle.model()));
            detailsMap.put(context.getString(R.string.starship_class), getSafeString(vehicle.class_()));
            detailsMap.put(context.getString(R.string.cost), getSafeString(vehicle.costInCredits()));
            detailsMap.put(context.getString(R.string.speed), getSafeString(vehicle.maxAtmospheringSpeed()));
            detailsMap.put(context.getString(R.string.length), getSafeString(vehicle.length()));
            detailsMap.put(context.getString(R.string.cargo_capacity), getSafeString(vehicle.cargoCapacity()));
            detailsMap.put(context.getString(R.string.crew), getSafeString(vehicle.crew()));
            detailsMap.put(context.getString(R.string.passengers), getSafeString(vehicle.passengers()));
            detailsMap.put(context.getString(R.string.consumables), getSafeString(vehicle.consumables()));

            if (vehicle.pilots() != null && vehicle.pilots().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (VehicleQuery.Pilot pilot : vehicle.pilots()) {
                    items.add(new SimpleQueryData(pilot.id(), pilot.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.pilots)), items);
            }

            if (vehicle.films() != null && vehicle.films().size() > 0) {
                List<SimpleQueryData> items = new ArrayList<>();
                for (VehicleQuery.Film film : vehicle.films()) {
                    items.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }
        }
    }

    private String getSafeString(Object value) {
        StringBuilder result = new StringBuilder();
        if (value != null) {

            if (value instanceof String)
                result = new StringBuilder((String) value);

            if (value instanceof Long || value instanceof Double)
                result = new StringBuilder(String.valueOf(value));

            if (value instanceof Enum)
                result = new StringBuilder(firstLetterCaps(value.toString()));

            if (value instanceof List) {
                int count = 0;
                for (Object item : (List) value) {
                    if (item instanceof String) {
                        if (count != 0)
                            result.append(", ");

                        result.append(firstLetterCaps((String) item));
                        count++;
                    }
                }
            }
        }
        return result.toString();
    }

    private String getListEnumValuesFormatted(Object enumValues) {
        if (enumValues != null && enumValues instanceof List) {
            StringBuilder result = new StringBuilder();
            int count = 0;

            for (Object enumValue : (List) enumValues) {
                if (enumValue instanceof Enum) {
                    if (count != 0)
                        result.append(", ");

                    result.append(getSafeString(enumValue));
                    count++;
                }
            }
            return result.toString();
        }

        return null;
    }

    /**
     * Makes the first letter caps and the rest lowercase.
     * <p>
     * <p>
     * <p>
     * For example <code>fooBar</code> becomes <code>Foobar</code>.
     *
     * @param data capitalize this
     * @return String
     */
    private String firstLetterCaps(String data) {
        String firstLetter = data.substring(0, 1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LinkedHashMap<String, String> getDetailsMap() {
        return detailsMap;
    }

    public LinkedHashMap<String, List<SimpleQueryData>> getRelatedItems() {
        return relatedItems;
    }

    public SwapiCategory getCategory() {
        return category;
    }

    public StorageReference getImageStorageReference() {
        String imageName = getTitle().replace('/', '_');

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        switch (category) {
            case FILM:
                return storageRef.child("films/" + imageName + ".jpg");
            case PEOPLE:
                return storageRef.child("people/" + imageName + ".jpg");
            case PLANET:
                return storageRef.child("planets/" + imageName + ".jpg");
            case SPECIES:
                return storageRef.child("species/" + imageName + ".jpg");
            case STARSHIP:
                return storageRef.child("starships/" + imageName + ".jpg");
            case VEHICLE:
                return storageRef.child("vehicles/" + imageName + ".jpg");
        }
        return null;
    }

    // region Parcelable


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.detailsMap.size());
        for (Map.Entry<String, String> entry : this.detailsMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
        dest.writeInt(this.relatedItems.size());
        for (Map.Entry<String, List<SimpleQueryData>> entry : this.relatedItems.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
    }

    protected AllQueryData(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        int detailsMapSize = in.readInt();
        this.detailsMap = new LinkedHashMap<>(detailsMapSize);
        for (int i = 0; i < detailsMapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.detailsMap.put(key, value);
        }
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : SwapiCategory.values()[tmpCategory];
        int relatedItemsSize = in.readInt();
        this.relatedItems = new LinkedHashMap<>(relatedItemsSize);
        for (int i = 0; i < relatedItemsSize; i++) {
            String key = in.readString();
            List<SimpleQueryData> value = in.createTypedArrayList(SimpleQueryData.CREATOR);
            this.relatedItems.put(key, value);
        }
    }

    public static final Creator<AllQueryData> CREATOR = new Creator<AllQueryData>() {
        @Override
        public AllQueryData createFromParcel(Parcel source) {
            return new AllQueryData(source);
        }

        @Override
        public AllQueryData[] newArray(int size) {
            return new AllQueryData[size];
        }
    };
    // endregion
}
