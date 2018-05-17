package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.apollographql.apollo.api.Response;

import java.util.ArrayList;
import java.util.List;

import api.AllFilmsQuery;
import api.AllPersonsQuery;
import api.AllPlanetsQuery;
import api.AllSpeciesQuery;
import api.AllStarshipsQuery;
import api.AllVehiclesQuery;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import timber.log.Timber;

/**
 * Mapping apollo result for the AllData GraphQL Responses
 */
public class CategoryItems implements Parcelable {
    private List<SimpleQueryData> queryDataList;

    public CategoryItems(Response response) {
        queryDataList = new ArrayList<>();

        if (response.data() != null) {

            if (response.data() instanceof AllFilmsQuery.Data) {
                for (AllFilmsQuery.AllFilm film : ((AllFilmsQuery.Data) response.data()).allFilms()) {
                    queryDataList.add(new SimpleQueryData(film.id(), film.title(), SwapiCategory.FILM));
                }
            } else if (response.data() instanceof AllPersonsQuery.Data) {
                for (AllPersonsQuery.AllPerson person : ((AllPersonsQuery.Data) response.data()).allPersons()) {
                    queryDataList.add(new SimpleQueryData(person.id(), person.name(), SwapiCategory.PEOPLE));
                }
            } else if (response.data() instanceof AllPlanetsQuery.Data) {
                for (AllPlanetsQuery.AllPlanet planet : ((AllPlanetsQuery.Data) response.data()).allPlanets()) {
                    queryDataList.add(new SimpleQueryData(planet.id(), planet.name(), SwapiCategory.PLANET));
                }
            } else if (response.data() instanceof AllSpeciesQuery.Data) {
                for (AllSpeciesQuery.AllSpecy species : ((AllSpeciesQuery.Data) response.data()).allSpecies()) {
                    queryDataList.add(new SimpleQueryData(species.id(), species.name(), SwapiCategory.SPECIES));
                }
            } else if (response.data() instanceof AllStarshipsQuery.Data) {
                for (AllStarshipsQuery.AllStarship starship : ((AllStarshipsQuery.Data) response.data()).allStarships()) {
                    queryDataList.add(new SimpleQueryData(starship.id(), starship.name(), SwapiCategory.STARSHIP));
                }
            } else if (response.data() instanceof AllVehiclesQuery.Data) {
                for (AllVehiclesQuery.AllVehicle vehicle : ((AllVehiclesQuery.Data) response.data()).allVehicles()) {
                    queryDataList.add(new SimpleQueryData(vehicle.id(), vehicle.name(), SwapiCategory.VEHICLE));
                }
            } else {
                Timber.d("Unknown response.data instance.");
            }
        } else {
            Timber.d("response data is null");
        }


    }

    public List<SimpleQueryData> getQueryDataList() {
        return queryDataList;
    }

    // region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.queryDataList);
    }

    protected CategoryItems(Parcel in) {
        this.queryDataList = new ArrayList<SimpleQueryData>();
        in.readList(this.queryDataList, SimpleQueryData.class.getClassLoader());
    }

    public static final Parcelable.Creator<CategoryItems> CREATOR = new Parcelable.Creator<CategoryItems>() {
        @Override
        public CategoryItems createFromParcel(Parcel source) {
            return new CategoryItems(source);
        }

        @Override
        public CategoryItems[] newArray(int size) {
            return new CategoryItems[size];
        }
    };
    // endregion
}
