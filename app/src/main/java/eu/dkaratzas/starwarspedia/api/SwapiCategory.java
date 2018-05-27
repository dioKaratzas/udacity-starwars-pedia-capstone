/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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