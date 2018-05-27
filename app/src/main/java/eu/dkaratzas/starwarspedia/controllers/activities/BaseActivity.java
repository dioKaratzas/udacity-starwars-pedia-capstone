package eu.dkaratzas.starwarspedia.controllers.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import eu.dkaratzas.starwarspedia.Constants;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.InAppBillingManager;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.StatusMessage;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements InAppBillingManager.AdListener {
    private FrameLayout mAdsContainer;
    protected GlobalApplication globalApplication;
    private AdView mAdView;
    private AlertDialog premiumDescAlert;

    abstract FrameLayout getAdsContainer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalApplication = (GlobalApplication) getApplicationContext();
        InAppBillingManager.addToListeners(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (premiumDescAlert != null) {
            if (premiumDescAlert.isShowing())
                premiumDescAlert.dismiss();
            premiumDescAlert = null;
        }

        InAppBillingManager.removeFromListeners(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (InAppBillingManager.getBillingProcessor() != null &&
                !InAppBillingManager.getBillingProcessor().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSetUpAds() {
        if (InAppBillingManager.isDisplayAds()) {
            mAdsContainer = getAdsContainer();

            if (mAdsContainer != null && mAdView == null) {
                mAdView = new AdView(getApplicationContext());
//            mAdView.setId(R.id.nativeExpressAdId);

                int height = Misc.pxToDp((int) getResources().getDimension(R.dimen.ads_container_height)); // or whatever is appropriate - make sure its >= ad minimum
                // set the size to the width of the screen
                mAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH, height));
                mAdView.setAdUnitId(Constants.NATIVE_AD_ID());

                FrameLayout.LayoutParams lp = new
                        FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);

                mAdView.setLayoutParams(lp);
                mAdsContainer.addView(mAdView);
                mAdView.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        mAdsContainer.setVisibility(View.VISIBLE);
                    }

                    public void onAdFailedToLoad(int var1) {
                        Timber.e("onAdFailedToLoad: %s", var1);
                    }
                });
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    @Override
    public void onRemoveAds() {
        if (mAdView != null) {
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            if (parent != null) {
                parent.removeView(mAdView);
                parent.setVisibility(View.GONE);
            }
            mAdView.setAdListener(null);
            mAdView.destroy();
            mAdView = null;
            Timber.d("Removed Ads");
        }
    }

    protected void showPurchaseDialog() {
        if (!InAppBillingManager.isReadyToPurchase()) {

            StatusMessage.show(this, getString(R.string.inapp_billing_not_available), true);

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final Activity activity = this;

            builder.setTitle(getString(R.string.remove_ads))
                    .setMessage(getString(R.string.remove_ads_description))
                    .setPositiveButton(getString(R.string.lets_go), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            InAppBillingManager.getBillingProcessor().purchase(activity, Constants.PREMIUM_PRODUCT_ID());
                        }
                    });

            premiumDescAlert = builder.create();
            premiumDescAlert.setCanceledOnTouchOutside(false);
            premiumDescAlert.show();

        }
    }
}
