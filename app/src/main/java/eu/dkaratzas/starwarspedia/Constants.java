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

package eu.dkaratzas.starwarspedia;

public class Constants {
    public static final String BASE_URL = "https://api.graphcms.com/simple/v1/swapi";
    public static final String IN_APP_BILLING_LICENSE_KEY = "your in app billing license key";
    public static final String MERCHANT_ID = "your merchant id";

    public static String PREMIUM_PRODUCT_ID() {
        if (BuildConfig.DEBUG)
            return "android.test.purchased";

        return "your product id";
    }

    public static String NATIVE_AD_ID() {
        if (BuildConfig.DEBUG)
            return "ca-app-pub-3940256099942544/6300978111";

        return "your ad banner id";
    }
}
