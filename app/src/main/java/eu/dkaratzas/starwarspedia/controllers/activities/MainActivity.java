package eu.dkaratzas.starwarspedia.controllers.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starsgl.widget.StarView;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.controllers.fragments.CategoryFragment;
import eu.dkaratzas.starwarspedia.libs.CustomDrawerButton;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.CategoryFragmentCallbacks {

    @BindView(R.id.starView)
    StarView mStarView;
    @BindView(R.id.ivLogo)
    ImageView mIvLogo;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.ivDrawerMenu)
    CustomDrawerButton mDrawerButton;

    private int selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Misc.setTransparentForDrawerLayout(this, mDrawerLayout, mIvLogo);

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
    public void onCategoryItemClicked(SwapiModel swapiModel) {
        Timber.d(String.valueOf(swapiModel));
    }

    @Override
    public void onCategoryDataLoading(boolean loading) {
        if (loading)
            mStarView.setSpeedFastTraveling();
        else
            mStarView.setSpeedNormal();
    }

    private void showCategory(SwapiCategory category) {
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
