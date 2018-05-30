![screen](../master/art/logo.png)

The StarWars Pedia app, was made as part of Udacity's [Android Developer Nanodegree Program](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801).
StarWars Pedia allows users to search a huge amount of information on all sorts of subjects within the official Star Wars universe ranging from Planets, Spaceships, Vehicles, People, Films and Species.

### Features
* Explore all the basic Star Wars category items such as Planets, Spaceships, Vehicles, People, Films and Species
* Access in extra details for any of the category items
* Share the item details with others
* Ability to add any category item to favorites list
* Homescreen widget including all of your favorite list items

## Download:

The app is published on PlayStore: [https://play.google.com/store/apps/details?id=eu.dkaratzas.starwarspedia](https://play.google.com/store/apps/details?id=eu.dkaratzas.starwarspedia)


## The review of my work
https://review.udacity.com/#!/reviews/1244639

```
Meets Specifications

You've nailed it! StarWars Pedia looks cool, works smoothly and serves its purpose well.
...
Congratulations! 🏆
```

and this is what i've earned!
![screen](../master/art/DionysiosKaratzasCertificate.png)

## The following **required** functionality is completed:

### Core Platform Development
* [x] App integrates a third-party library.
* [x] App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash.
* [x] App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
* [x] App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
* [x] App provides a widget to provide relevant information to the user on the home screen.

### Google Play Services

* [x] App implements Google Analytics service
* [x] App implements Google Ads service
* [x] Each service imported in the build.gradle is used in the app.

### Material Design

* [x] App theme extends AppCompat.
* [x] App uses an app bar and associated toolbars.
* [x] App uses standard and simple transitions between activities.

### Building

* [x] App builds from a clean repository checkout with no additional configuration.
* [x] App builds and deploys using the installRelease Gradle task.
* [x] App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
* [x] All app dependencies are managed by Gradle.

### Data Persistence

* [x] App stores data locally either by implementing a ContentProvider OR using Firebase Realtime Database. No third party frameworks may be used.
* [x] It it performs short duration, on-demand requests(such as search), app uses an AsyncTask.
* [x] App uses a Loader to move its data to its views.


## How to Work with the Source

* In order to get Crashlytics to work, you need to put your Fabric secret key on the AndroidManifest.xml file 
```AndroidManifest.xml
<meta-data
    android:name="io.fabric.ApiKey"
    android:value="your api key" />
```


* Firebase Storage is used to store and retrieve the StarWars data images since the API doesn’t provide image resources.
You must add your Firebase google-services.json file into the app/ folder.


Libraries
---------
* [Apollo GraphQL Client for Android](https://github.com/apollographql/apollo-android)
* [Firebase Storage](https://firebase.google.com/docs/storage/android/start)
* [Glide](https://github.com/bumptech/glide)
* [Butter Knife](https://github.com/JakeWharton/butterknife)
* [Timber](https://github.com/JakeWharton/timber)
* [LeakCanary](https://github.com/square/leakcanary)
* [StarWars.Android by Yalantis](https://github.com/Yalantis/StarWars.Android)

License
-------
Copyright 2018 Dionysios Karatzas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
