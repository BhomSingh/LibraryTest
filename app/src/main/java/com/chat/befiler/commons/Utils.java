package com.chat.befiler.commons;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signalrtestandroid.R;

import java.io.File;
import java.io.InputStream;

public class Utils {

    private static final int maxFileSize = 2621440;
    private static final int maxImageFileSize = 1000000;

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void customeTost(String msg, Activity context, View view) {
        if(view!= null){
            View layout = context.getLayoutInflater().inflate(R.layout.custom_toast,(ViewGroup) view.findViewById(R.id.custom_toast_container));
            TextView text = layout.findViewById(R.id.MyText);
            text.setText(msg);
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

    public static boolean MaxSizeImage(int filesize) {
        boolean temp = false;
        // 2.5 mb
        temp = filesize > maxFileSize;
        return temp;
    }

    public static boolean MaxSizeImageCheck(int filesize) {
        boolean temp = false;
        // 1 mb
        temp = filesize > maxImageFileSize;
        return temp;
    }

    public static boolean isCheckFileSize(Uri uri, Context context){

        Boolean isFileSizeExceed= false;
        int dataSize=0;
        String scheme = uri.getScheme();
        if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                dataSize = fileInputStream.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isFileSizeExceed= !Utils.MaxSizeImage(dataSize);
        }
        else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File f=null;
            String path = uri.getPath();
            try {
                f = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isFileSizeExceed = !Utils.MaxSizeImage(dataSize);
        }
        return isFileSizeExceed;
    }

    public static boolean isFileLessThan2MB(File file) {
        int maxFileSize = 2 * 1024 * 1024;
        Long l = file.length();
        String fileSize = l.toString();
        int finalFileSize = Integer.parseInt(fileSize);
        return finalFileSize <= maxFileSize;
    }

    public static boolean isCheckImageSize(Uri uri,Context context){

        Boolean isFileSizeExceed= false;
        int dataSize=0;
        String scheme = uri.getScheme();
        if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                dataSize = fileInputStream.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isFileSizeExceed= !Utils.MaxSizeImageCheck(dataSize);
        }
        else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File f=null;
            String path = uri.getPath();
            try {
                f = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isFileSizeExceed = !Utils.MaxSizeImageCheck(dataSize);
        }
        return isFileSizeExceed;
    }

}
