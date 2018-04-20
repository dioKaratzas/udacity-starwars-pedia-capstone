package eu.dkaratzas.starwarspedia.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {
    private SwapiModel mSwapiModel;
    public static final String EXTRA_DATA_TO_DISPLAY = "extra_data";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_DATA_TO_DISPLAY)) {
            mSwapiModel = bundle.getParcelable(EXTRA_DATA_TO_DISPLAY);
            mSwapiModel.getDetailsToDisplay(new StarWarsApiCallback<Map<String, Object>>() {
                @Override
                public void onResponse(Map<String, Object> result) {
                    Timber.d(result.toString());
                }

                @Override
                public void onCancel() {

                }
            }, getApplicationContext());
            Timber.d(String.valueOf(mSwapiModel));
        } else {
            throw new IllegalArgumentException("Must provide a SwapiModel as intent extra to display it's data!");
        }
        setSupportActionBar(mToolbar);
    }

}
