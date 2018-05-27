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
    protected void onCreate(Bundle savedInstanceState) {
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

        Bundle bundle = getIntent().getExtras();

        // If turn the screen orientation then the savedInstanceState is not null.
        // In this condition, do not need to add new fragment again.
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

    // endregion

}
