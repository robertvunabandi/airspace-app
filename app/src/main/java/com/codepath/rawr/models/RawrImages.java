package com.codepath.rawr.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;

/**
 * Created by robertvunabandi on 8/2/17.
 */

public class RawrImages {
    public AsyncHttpClient client = new AsyncHttpClient();
    public static final int MAX_FILE_SIZE = 10; // 10 megabytes


    public static byte[] convertImageToByteArray(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageByte = baos.toByteArray();
        return imageByte;
    }
    public static byte[] convertStringToByteArray(String image) {
        return image.getBytes();
    }
    public static String convertImageToStringEncode64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
    }
    public static String convertByteArrayToStringEncode64(byte[] imageByte) {
        return new String(imageByte);
    }

    public static Bitmap convertByteArrayToImage(byte[] imageByte) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
    }
    public static Bitmap convertString64ToImage(String image) throws Exception {
        byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
    }

    public static RequestParams getParamsSaveProfileImage(String user_id, String url) {
        RequestParams params = new RequestParams();
        params.put("user_id", user_id);
        params.put("url", url);
        return params;
    }
    public static RequestParams getParamsSaveRequestImage(String request_id, String url) {
        RequestParams params = new RequestParams();
        params.put("user_id", request_id);
        params.put("url", url);
        return params;
    }

    public static RequestParams getParamsGetProfileImage(String user_id) {
        RequestParams params = new RequestParams();
        params.put("user_id", user_id);
        return params;
    }
    public static RequestParams getParamsGetTravelNoticeImage(String travel_notice_id) {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", travel_notice_id);
        return params;
    }

    public static String getImageType(Bitmap image) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(RawrImages.convertImageToByteArray(image), 0, 0, bounds);
        return bounds.outMimeType; // this may be totally wrong
    }

    public static int getImageSize(Bitmap image) {
        // OK: returns the size of an image bitmap
        return image.getByteCount();
    }

    public static boolean isSizeOk(int size) {
        // OK: checks if the size of an image bitmap is ok to saved
        return size < 1024 * 1024 * MAX_FILE_SIZE;
    }
}
