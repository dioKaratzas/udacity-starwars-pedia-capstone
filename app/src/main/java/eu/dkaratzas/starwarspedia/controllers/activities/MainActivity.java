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

package eu.dkaratzas.starwarspedia.controllers.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.leakcanary.RefWatcher;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starsgl.widget.StarView;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.InAppBillingManager;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.ApolloManager;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.controllers.fragments.CategoryFragment;
import eu.dkaratzas.starwarspedia.controllers.fragments.FavouritesFragment;
import eu.dkaratzas.starwarspedia.libs.CustomDrawerButton;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.StatusMessage;
import eu.dkaratzas.starwarspedia.models.AllQueryData;
import eu.dkaratzas.starwarspedia.models.SimpleQueryData;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, CategoryFragment.CategoryFragmentCallbacks, FavouritesFragment.FavouritesFragmentCallbacks {

    public static final String EXTRA_FAVOURITE_DATA = "eu.dkaratzas.starwarspedia.extra.FAVOURITE_DATA";
    private static final int LOADER_ID = 90;

    @BindView(R.id.starView)
    StarView mStarView;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.ivDrawerMenu)
    CustomDrawerButton mDrawerButton;
    @BindView(R.id.mainContentLayout)
    View mMainContent;
    @Nullable
    @BindView(R.id.appBar)
    AppBarLayout mAppBar;

    private int mSelectedItemId;
    private boolean mOnFavouriteCategory;
    private Dialog loadingDialog;

    // region Activity Lifecycle
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        Misc.setTransparentForDrawerLayout(this, mDrawerLayout, mMainContent);

        mDrawerButton.setDrawerLayout(mDrawerLayout);
        mDrawerButton.getDrawerLayout().addDrawerListener(mDrawerButton);
        mDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerButton.changeState();
            }
        });

        mNavView.setNavigationItemSelectedListener(this);
        mNavView.getMenu().findItem(R.id.nav_premium).setVisible(InAppBillingManager.isDisplayAds());

        final Bundle bundle = getIntent().getExtras();

        // If turn the screen orientation then the savedInstanceState is not null.
        // In this condition, do not need to add new fragment again.
        showPrivacyPolicyDialog(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean b) {
                if (savedInstanceState == null) {
                    if (bundle != null && bundle.containsKey(EXTRA_FAVOURITE_DATA)) {
                        loadItemDetailsAndLaunchActivity((SimpleQueryData) bundle.getParcelable(EXTRA_FAVOURITE_DATA), getString(R.string.favourites));
                        mNavView.setCheckedItem(R.id.nav_favourites);
                        switchToFavouritesCategory();
                    } else {
                        selectDefaultCategory();
                    }
                }
            }

            @Override
            public void onCancel() {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        // If we return back from details activity reload the favourites category
        // because the item might removed or added on the favourites list
        if (mOnFavouriteCategory)
            switchToFavouritesCategory();
    }

    @Override
    FrameLayout getAdsContainer() {
        FrameLayout frameLayout = findViewById(R.id.ads_container);
        return frameLayout;
    }

    @Override
    protected void onDestroy() {
        RefWatcher refWatcher = GlobalApplication.getRefWatcher(this);
        refWatcher.watch(this);

        hideLoadingDialog();

        super.onDestroy();
    }

    // endregion

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (mSelectedItemId != id) {

            StatusMessage.hide();

            if (id != R.id.nav_premium)
                mSelectedItemId = id;

            switch (id) {
                case R.id.nav_people:
                    switchToCategory(SwapiCategory.PEOPLE);
                    break;
                case R.id.nav_films:
                    switchToCategory(SwapiCategory.FILM);
                    break;
                case R.id.nav_sparships:
                    switchToCategory(SwapiCategory.STARSHIP);
                    break;
                case R.id.nav_vehicles:
                    switchToCategory(SwapiCategory.VEHICLE);
                    break;
                case R.id.nav_species:
                    switchToCategory(SwapiCategory.SPECIES);
                    break;
                case R.id.nav_planets:
                    switchToCategory(SwapiCategory.PLANET);
                    break;
                case R.id.nav_favourites:
                    switchToFavouritesCategory();
                    break;
                case R.id.nav_premium:
                    showPurchaseDialog();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return false;
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSetUpAds() {
        super.onSetUpAds();

        mNavView.getMenu().findItem(R.id.nav_premium).setVisible(true);
    }

    @Override
    public void onRemoveAds() {
        super.onRemoveAds();

        mNavView.getMenu().findItem(R.id.nav_premium).setVisible(false);
    }

    // region Fragment Callbacks
    @Override
    public void onFavouriteItemClicked(SimpleQueryData queryData) {
        loadItemDetailsAndLaunchActivity(queryData, getString(R.string.favourites));
    }

    @Override
    public void onFavouriteDataLoading(boolean loading) {
        onLoadingData(loading);
    }

    /**
     * Fetch the item by ID and start DetailsActivity to show its details
     *
     * @param queryData Items result
     */
    @Override
    public void onCategoryItemClicked(final SimpleQueryData queryData) {
        loadItemDetailsAndLaunchActivity(queryData, queryData.getCategory().getString(getApplicationContext()));
    }

    @Override
    public void onCategoryDataLoading(boolean loading) {
        onLoadingData(loading);
    }

    // endregion

    // region Methods
    private void switchToCategory(final SwapiCategory category) {
        mOnFavouriteCategory = false;

        // if we are on small screen devices and AppBarLayout exists expand it
        if (mAppBar != null)
            mAppBar.setExpanded(true, true);

        // Destroy any remaining loader
        destroyLoaders();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_upward_in, R.anim.slide_down_out, R.anim.slide_upward_in, R.anim.slide_down_out)
                .replace(R.id.container, CategoryFragment.newInstance(category))
                .commit();
    }

    private void switchToFavouritesCategory() {
        mOnFavouriteCategory = true;

        // if we are on small screen devices and AppBarLayout exists expand it
        if (mAppBar != null)
            mAppBar.setExpanded(true, true);

        // Destroy any remaining loader
        destroyLoaders();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_upward_in, R.anim.slide_down_out, R.anim.slide_upward_in, R.anim.slide_down_out)
                .replace(R.id.container, FavouritesFragment.newInstance())
                .commit();
    }

    private void destroyLoaders() {
        getSupportLoaderManager().destroyLoader(CategoryFragment.LOADER_ID);
        getSupportLoaderManager().destroyLoader(FavouritesFragment.LOADER_ID);
    }

    private void selectDefaultCategory() {
        MenuItem menuItem = mNavView.getMenu().getItem(0);
        menuItem.setChecked(true);
        onNavigationItemSelected(menuItem);
    }

    private void onLoadingData(boolean loading) {
        if (loading)
            mStarView.setSpeedFastTraveling();
        else
            mStarView.setSpeedNormal();
    }

    private void loadItemDetailsAndLaunchActivity(final SimpleQueryData queryData, final String categoryTitle) {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        if (Misc.isNetworkAvailable(getApplicationContext())) {
            if (queryData != null) {

                showLoadingDialog();

                ApolloManager.instance().fetchSwapiItem(this, queryData.getId(), queryData.getCategory(), getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<AllQueryData>() {
                    @Override
                    public void onResponse(AllQueryData result) {
                        getSupportLoaderManager().destroyLoader(LOADER_ID);
                        hideLoadingDialog();

                        if (result == null) {
                            StatusMessage.show(MainActivity.this, getString(R.string.error_getting_data));
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(DetailActivity.EXTRA_DATA_TO_DISPLAY, result);
                            bundle.putString(DetailActivity.EXTRA_CURRENT_CATEGORY_TITLE, categoryTitle);
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });

            }
        } else {
            StatusMessage.show(this, getResources().getString(R.string.no_internet));
        }
    }

    private void showLoadingDialog() {
        hideLoadingDialog();

        loadingDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        loadingDialog.setContentView(R.layout.loading_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void showPrivacyPolicyDialog(PrivacyPolicyDialog.OnClickListener listener) {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "https://dnkaratzas.github.io/udacity-starwars-pedia-capstone/terms_and_conditions",
                "https://dnkaratzas.github.io/udacity-starwars-pedia-capstone/privacy_policy");

        dialog.addPoliceLine("By downloading or using the app, these terms will automatically apply to you – you should make sure therefore that you read them carefully before using the app. You’re not allowed to copy, or modify the app, any part of the app, or our trademarks in any way. You’re not allowed to attempt to extract the source code of the app, and you also shouldn’t try to translate the app into other languages, or make derivative versions. The app itself, and all the trade marks, copyright, database rights and other intellectual property rights related to it, still belong to Dionysis Karatzas.");
        dialog.addPoliceLine("Dionysis Karatzas is committed to ensuring that the app is as useful and efficient as possible. For that reason, we reserve the right to make changes to the app or to charge for its services, at any time and for any reason. We will never charge you for the app or its services without making it very clear to you exactly what you’re paying for.");
        dialog.addPoliceLine("The StarWars Pedia app stores and processes personal data that you have provided to us, in order to provide my Service. It’s your responsibility to keep your phone and access to the app secure. We therefore recommend that you do not jailbreak or root your phone, which is the process of removing software restrictions and limitations imposed by the official operating system of your device. It could make your phone vulnerable to malware/viruses/malicious programs, compromise your phone’s security features and it could mean that the StarWars Pedia app won’t work properly or at all.");
        dialog.addPoliceLine("You should be aware that there are certain things that Dionysis Karatzas will not take responsibility for. Certain functions of the app will require the app to have an active internet connection. The connection can be Wi-Fi, or provided by your mobile network provider, but Dionysis Karatzas cannot take responsibility for the app not working at full functionality if you don’t have access to Wi-Fi, and you don’t have any of your data allowance left.");
        dialog.addPoliceLine("If you’re using the app outside of an area with Wi-Fi, you should remember that your terms of the agreement with your mobile network provider will still apply. As a result, you may be charged by your mobile provider for the cost of data for the duration of the connection while accessing the app, or other third party charges. In using the app, you’re accepting responsibility for any such charges, including roaming data charges if you use the app outside of your home territory (i.e. region or country) without turning off data roaming. If you are not the bill payer for the device on which you’re using the app, please be aware that we assume that you have received permission from the bill payer for using the app.");
        dialog.addPoliceLine("Along the same lines, Dionysis Karatzas cannot always take responsibility for the way you use the app i.e. You need to make sure that your device stays charged – if it runs out of battery and you can’t turn it on to avail the Service, Dionysis Karatzas cannot accept responsibility.");
        dialog.addPoliceLine("With respect to Dionysis Karatzas’s responsibility for your use of the app, when you’re using the app, it’s important to bear in mind that although we endeavour to ensure that it is updated and correct at all times, we do rely on third parties to provide information to us so that we can make it available to you. Dionysis Karatzas accepts no liability for any loss, direct or indirect, you experience as a result of relying wholly on this functionality of the app.");
        dialog.addPoliceLine("At some point, we may wish to update the app. The app is currently available on Android – the requirements for system(and for any additional systems we decide to extend the availability of the app to) may change, and you’ll need to download the updates if you want to keep using the app. Dionysis Karatzas does not promise that it will always update the app so that it is relevant to you and/or works with the Android version that you have installed on your device. However, you promise to always accept updates to the application when offered to you, We may also wish to stop providing the app, and may terminate use of it at any time without giving notice of termination to you. Unless we tell you otherwise, upon any termination, (a) the rights and licenses granted to you in these terms will end; (b) you must stop using the app, and (if needed) delete it from your device.");

        dialog.setOnClickListener(listener);

        dialog.show();
    }

    // endregion

}
