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

package eu.dkaratzas.starwarspedia;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_OK;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

public class InAppBillingManager implements BillingProcessor.IBillingHandler {

    private static volatile InAppBillingManager sharedInstance;

    private Context mContext;
    private BillingProcessor mBillingProcessor;
    private List<AdListener> mAdListeners = new ArrayList<>();

    private boolean mDisplayAds = false;
    private boolean mReadyToPurchase = false;

    public static void init(Context context) {
        if (sharedInstance == null) {
            synchronized (InAppBillingManager.class) {
                sharedInstance = new InAppBillingManager(context);
            }
        }
    }

    private InAppBillingManager(Context context) {
        mContext = context;

        if (!BillingProcessor.isIabServiceAvailable(context)) {
            Toast.makeText(context, context.getString(R.string.inapp_billing_not_available), Toast.LENGTH_LONG).show();
            mDisplayAds = true;
        }

        mBillingProcessor = new BillingProcessor(context, Constants.IN_APP_BILLING_LICENSE_KEY, Constants.MERCHANT_ID, this);
    }

    // region BillingProcessor Callbacks
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        if (productId.equals(Constants.PREMIUM_PRODUCT_ID()) && mBillingProcessor.isPurchased(Constants.PREMIUM_PRODUCT_ID())) {
            mDisplayAds = false;
            removeAds();
            Toast.makeText(mContext, R.string.thank_you, Toast.LENGTH_LONG).show();

            release();
        }

        Timber.d("onProductPurchased: %s", productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
            mDisplayAds = false;
            removeAds();

            release();

        } else if (errorCode != BILLING_RESPONSE_RESULT_USER_CANCELED && errorCode != BILLING_RESPONSE_RESULT_OK) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Current Thread is Main Thread.
                Toast.makeText(mContext, R.string.unexpected_error, Toast.LENGTH_LONG).show();
            }
        }
        Timber.d("onBillingError: %s", Integer.toString(errorCode));
        Crashlytics.log("onBillingError: " + Integer.toString(errorCode));
        Crashlytics.logException(error);
    }

    @Override
    public void onBillingInitialized() {
        mReadyToPurchase = true;

        if (BuildConfig.DEBUG)
            mBillingProcessor.consumePurchase(Constants.PREMIUM_PRODUCT_ID()); //Remove purchased item from buyer

        if (mBillingProcessor.loadOwnedPurchasesFromGoogle()) {
            if (!mBillingProcessor.isPurchased(Constants.PREMIUM_PRODUCT_ID())) {
                mDisplayAds = true;
                setUpAds();
            } else {
                mDisplayAds = false;
                removeAds();

                release();
            }
        }
    }
    // endregion

    // region Public Methods
    @Nullable
    public static BillingProcessor getBillingProcessor() {
        return getSharedInstance().mBillingProcessor;
    }

    public static boolean isDisplayAds() {
        return getSharedInstance().mDisplayAds;
    }

    public static boolean isReadyToPurchase() {
        return getSharedInstance().mReadyToPurchase;
    }

    public static void addToListeners(AdListener adListener) {
        if (adListener != null)
            getSharedInstance().mAdListeners.add(adListener);

        if (isDisplayAds())
            getSharedInstance().setUpAds();
    }

    public static void removeFromListeners(AdListener adListener) {
        if (adListener != null)
            getSharedInstance().mAdListeners.remove(adListener);
    }

    public static void release() {
        if (sharedInstance.mBillingProcessor != null) {
            sharedInstance.mBillingProcessor.release();
            sharedInstance.mBillingProcessor = null;
            sharedInstance.mReadyToPurchase = false;
        }
    }
    // endregion

    // region Private Methods

    private static InAppBillingManager getSharedInstance() {
        // Prevent from the reflection api.
        if (sharedInstance == null) {
            throw new RuntimeException("Must call init first.");
        }

        return sharedInstance;
    }

    private void setUpAds() {

        if (mAdListeners != null && mAdListeners.size() > 0) {

            for (AdListener adListener : mAdListeners) {

                adListener.onSetUpAds();

            }

        }
    }

    private void removeAds() {

        if (mAdListeners != null && mAdListeners.size() > 0) {

            for (AdListener adListener : mAdListeners) {

                adListener.onRemoveAds();

            }

        }
    }
    // endregion

    /* Interfaces */
    public interface AdListener {
        void onSetUpAds();

        void onRemoveAds();
    }
}
