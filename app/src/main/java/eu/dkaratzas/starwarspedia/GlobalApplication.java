package eu.dkaratzas.starwarspedia;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class GlobalApplication extends Application {
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        GlobalApplication application = (GlobalApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
