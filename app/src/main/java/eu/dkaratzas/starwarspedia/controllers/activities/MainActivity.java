package eu.dkaratzas.starwarspedia.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starsgl.widget.StarView;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.ApolloManager;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.controllers.fragments.CategoryFragment;
import eu.dkaratzas.starwarspedia.libs.CustomDrawerButton;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.StatusMessage;
import eu.dkaratzas.starwarspedia.models.AllQueryData;
import eu.dkaratzas.starwarspedia.models.QueryData;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CategoryFragment.CategoryFragmentCallbacks {

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

    private int selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        if (savedInstanceState == null) {
            selectDefaultCategory();
        }
    }

    @Override
    protected void onDestroy() {
        RefWatcher refWatcher = GlobalApplication.getRefWatcher(this);
        refWatcher.watch(this);

        super.onDestroy();
    }

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (selectedItemId != id) {

            selectedItemId = id;

            switch (id) {
                case R.id.nav_people:
                    showCategory(SwapiCategory.PEOPLE);
                    break;
                case R.id.nav_films:
                    showCategory(SwapiCategory.FILM);
                    break;
                case R.id.nav_sparships:
                    showCategory(SwapiCategory.STARSHIP);
                    break;
                case R.id.nav_vehicles:
                    showCategory(SwapiCategory.VEHICLE);
                    break;
                case R.id.nav_species:
                    showCategory(SwapiCategory.SPECIES);
                    break;
                case R.id.nav_planets:
                    showCategory(SwapiCategory.PLANET);
                    break;
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onCategoryItemClicked(QueryData queryData) {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        if (queryData != null) {
            ApolloManager.instance().fetchSwapiItem(this, queryData.getId(), queryData.getCategory(), getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<AllQueryData>() {
                @Override
                public void onResponse(AllQueryData result) {
                    getSupportLoaderManager().destroyLoader(LOADER_ID);
                    if (result == null) {
                        StatusMessage.show(MainActivity.this, getString(R.string.error_getting_data));
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DetailActivity.EXTRA_DATA_TO_DISPLAY, result);
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });

        }
    }

    @Override
    public void onCategoryDataLoading(boolean loading) {
        if (loading)
            mStarView.setSpeedFastTraveling();
        else
            mStarView.setSpeedNormal();
    }

    private void showCategory(SwapiCategory category) {
        // if we are on small screen devices and AppBarLayout exists expand it
        if (mAppBar != null)
            mAppBar.setExpanded(true, true);

        // Destroy the Loader of the fragment
        getSupportLoaderManager().destroyLoader(CategoryFragment.LOADER_ID);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_upward_in, R.anim.slide_down_out, R.anim.slide_upward_in, R.anim.slide_down_out)
                .replace(R.id.container, CategoryFragment.newInstance(category))
                .commit();
    }

    private void selectDefaultCategory() {
        MenuItem menuItem = mNavView.getMenu().getItem(0);
        menuItem.setChecked(true);
        onNavigationItemSelected(menuItem);
    }
}
