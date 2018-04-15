package eu.dkaratzas.starwarspedia.controllers.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starsgl.widget.StarView;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.controllers.fragments.CategoryFragment;
import eu.dkaratzas.starwarspedia.libs.Animations;
import eu.dkaratzas.starwarspedia.libs.CustomDrawerButton;
import eu.dkaratzas.starwarspedia.libs.Misc;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnCategoryClickedListener {

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
    @BindView(R.id.statusMessageContainer)
    CardView mStatusMessageContainer;
    @BindView(R.id.tvStatusMessage)
    TextView mTvStatusMessage;

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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_people) {
            showCategory(SwapiCategory.PEOPLE);
        } else if (id == R.id.nav_films) {
            showCategory(SwapiCategory.FILM);
        } else if (id == R.id.nav_sparships) {
            showCategory(SwapiCategory.STARSHIP);
        } else if (id == R.id.nav_vehicles) {
            showCategory(SwapiCategory.VEHICLE);
        } else if (id == R.id.nav_species) {
            showCategory(SwapiCategory.SPECIES);
        } else if (id == R.id.nav_planets) {
            showCategory(SwapiCategory.PLANET);

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCategoryClicked(Uri uri) {

    }

    private void showCategory(SwapiCategory category) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_downward, 0, R.anim.slide_downward, 0)
                .add(R.id.container, CategoryFragment.newInstance(category), "greetings")
                .commit();
    }

    private void showStatus(String message) {
        mTvStatusMessage.setText(message);
        Animations.SlideInUpAnimation(mStatusMessageContainer);
    }

    private void hideStatus() {
        Animations.SlideOutDownAnimation(mStatusMessageContainer);
    }

    private void selectDefaultCategory() {
        MenuItem menuItem = mNavView.getMenu().getItem(0);
        menuItem.setChecked(true);
        onNavigationItemSelected(menuItem);
    }
}
