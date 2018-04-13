package eu.dkaratzas.starwarspedia;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starsgl.widget.StarView;
import eu.dkaratzas.starwarspedia.api.ApiCallback;
import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.libs.CustomDrawerButton;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.models.Category;
import eu.dkaratzas.starwarspedia.models.People;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        StarWarsApi.getApi().getAllPeopleAtPage(1, new ApiCallback<Category<People>>() {
            @Override
            public void onResponse(Category<People> result) {
                Logger.d(result.results.get(0).toString());
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            ((StarView) findViewById(R.id.starView)).setSpeedFastTraveling();
        } else if (id == R.id.nav_gallery) {
            ((StarView) findViewById(R.id.starView)).setSpeedNormal();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
