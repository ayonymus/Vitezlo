package org.szvsszke.vitezlo2018;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by g on 2016.04.09..
 *
 * Helper class for getting perimissions during runtime.
 */
public class PermissionHelper {

    public static final int LOCATION_REQUEST = 1337;
    public static final int WRITE_REQUEST = 1338;

    public static final String COARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String[] LOC_PERMISSIONS = new String[]{COARSE, FINE};

    public static void requestLocation(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(LOC_PERMISSIONS, LOCATION_REQUEST);
        }
    }

    public static boolean hasCoarse(Context context) {
        return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context);
    }

    public static boolean hasFineLocation(Context context) {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, context);
    }

    public static boolean hasPermission(String permission, Context context) {
        return(PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, permission));
    }
}
