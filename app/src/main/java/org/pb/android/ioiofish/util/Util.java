package org.pb.android.ioiofish.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class Util {

    public static int getDisplayHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

}
