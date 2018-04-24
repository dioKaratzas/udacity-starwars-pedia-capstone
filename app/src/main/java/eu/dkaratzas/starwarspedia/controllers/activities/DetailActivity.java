package eu.dkaratzas.starwarspedia.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.libs.GlideApp;
import eu.dkaratzas.starwarspedia.models.SwapiModel;

public class DetailActivity extends AppCompatActivity {
    private static final int LOADER_ID = 90;

    private SwapiModel mSwapiModel;
    public static final String EXTRA_DATA_TO_DISPLAY = "extra_data";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ivThumb)
    ImageView mIvThumb;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvDetails)
    TextView mTvDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_DATA_TO_DISPLAY)) {
            mSwapiModel = bundle.getParcelable(EXTRA_DATA_TO_DISPLAY);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            publishUI();

        } else {
            throw new IllegalArgumentException("Must provide a SwapiModel as intent extra to display it's data!");
        }
    }

    private void publishUI() {
        getSupportActionBar().setTitle(mSwapiModel.getCategory().getString(getApplicationContext()));
        mTvTitle.setText(mSwapiModel.getTitle());

        GlideApp.with(this)
                .load(mSwapiModel.getImageStorageReference())
                .into(mIvThumb);

        Map<String, String> detailsMap = mSwapiModel.getDetailsToDisplay(getApplicationContext());
        StringBuilder details = new StringBuilder();
        for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
            details.append(String.format("<b>%s:</b> %s<br>", entry.getKey(), entry.getValue()));
        }
        mTvDetails.setText(Html.fromHtml(details.toString()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getSupportLoaderManager().destroyLoader(LOADER_ID);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        super.onBackPressed();
    }
}
