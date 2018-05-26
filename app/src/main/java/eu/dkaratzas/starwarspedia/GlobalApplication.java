package eu.dkaratzas.starwarspedia;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class GlobalApplication extends Application implements BillingProcessor.IBillingHandler {
    private RefWatcher refWatcher;
    private BillingProcessor billingProcessor;
    private List<AdListener> adListeners = new ArrayList<>();

    private boolean displayAds = false;
    private boolean readyToPurchase = false;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        if (BuildConfig.DEBUG) {
            if (billingProcessor == null) {
                if (!BillingProcessor.isIabServiceAvailable(this)) {
                    Toast.makeText(this, getString(R.string.inapp_billing_not_available), Toast.LENGTH_LONG).show();
                }
                billingProcessor = new BillingProcessor(this, Constants.IN_APP_BILLING_LICENSE_KEY, Constants.MERCHANT_ID, this);
            }
        }
    }

    @Override
    public void onTerminate() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onTerminate();
    }

    public static RefWatcher getRefWatcher(Context context) {
        GlobalApplication application = (GlobalApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    // region BillingProcessor Callbacks
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        if (productId.equals(Constants.PREMIUM_PRODUCT_ID) && billingProcessor.isPurchased(Constants.PREMIUM_PRODUCT_ID)) {
            displayAds = false;
            removeAds();
            Toast.makeText(getApplicationContext(), R.string.thank_you, Toast.LENGTH_LONG).show();
        }

        Timber.d("onProductPurchased: %s", productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
            displayAds = false;
            removeAds();

        } else if (errorCode != BILLING_RESPONSE_RESULT_USER_CANCELED && errorCode != BILLING_RESPONSE_RESULT_OK) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Current Thread is Main Thread.
                Toast.makeText(getApplicationContext(), R.string.unexpected_error, Toast.LENGTH_LONG).show();
            }
        }
        Timber.d("onBillingError: %s", Integer.toString(errorCode));
        Crashlytics.log("onBillingError: " + Integer.toString(errorCode));
        Crashlytics.logException(error);
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        billingProcessor.consumePurchase(Constants.PREMIUM_PRODUCT_ID); //Remove purchased item from buyer

        if (billingProcessor.loadOwnedPurchasesFromGoogle()) {
            if (!billingProcessor.isPurchased(Constants.PREMIUM_PRODUCT_ID)) {
                displayAds = true;
                setUpAds();
            } else {
                displayAds = false;
                removeAds();
            }
        }
    }
    // endregion

    private void setUpAds() {

        if (adListeners != null && adListeners.size() > 0) {

            for (AdListener adListener : adListeners) {

                adListener.onSetUpAds();

            }

        }
    }

    private void removeAds() {

        if (adListeners != null && adListeners.size() > 0) {

            for (AdListener adListener : adListeners) {

                adListener.onRemoveAds();

            }

        }
    }

    public BillingProcessor getBillingProcessor() {
        return billingProcessor;
    }

    public boolean isDisplayAds() {
        return displayAds;
    }

    public boolean isReadyToPurchase() {
        return readyToPurchase;
    }

    public void addToListeners(AdListener adListener) {
        if (adListener != null)
            adListeners.add(adListener);

        setUpAds();
    }

    public void removeFromListeners(AdListener adListener) {
        if (adListener != null)
            adListeners.remove(adListener);
    }

    /* Interfaces */
    public interface AdListener {
        void onSetUpAds();

        void onRemoveAds();
    }

}
