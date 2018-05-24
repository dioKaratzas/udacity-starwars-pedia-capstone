package eu.dkaratzas.starwarspedia.libs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

public class Misc {

    public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @Nullable View viewToAddTopMargin) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);

        if (viewToAddTopMargin != null && viewToAddTopMargin.getLayoutParams() instanceof DrawerLayout.LayoutParams) {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) viewToAddTopMargin.getLayoutParams();

            params.topMargin += getStatusBarHeight(activity.getApplicationContext());
            viewToAddTopMargin.setLayoutParams(params);
        }

        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else
            return "";
    }

    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * Returns a handy span count starting from 2 based on a preferred item width
     *
     * @param columnWidth      The preferred column width
     * @param paddingLeftRight The padding that applies left of the first item and right of the last
     * @return SpanCount
     */
    public static int getHandySpanCount(Context context, int columnWidth, int paddingLeftRight) {
        int totalSpace = context.getResources().getDisplayMetrics().widthPixels - (paddingLeftRight * 2);
        return (int) Math.max(2, (long) Math.ceil((double) totalSpace / columnWidth));
    }
}
