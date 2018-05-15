package eu.dkaratzas.starwarspedia.api;

import android.content.Context;

import eu.dkaratzas.starwarspedia.R;

public enum SwapiCategory {
    FILM(R.string.films),
    PLANET(R.string.planets),
    SPECIES(R.string.species),
    STARSHIP(R.string.starships),
    VEHICLE(R.string.vehicles),
    PEOPLE(R.string.people);

    private final int stringResourceId;

    SwapiCategory(int s) {
        stringResourceId = s;
    }

    public String getString(Context context) {
        return context.getResources().getString(stringResourceId);
    }
}