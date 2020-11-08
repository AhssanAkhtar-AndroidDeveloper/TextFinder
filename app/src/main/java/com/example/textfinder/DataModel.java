package com.example.textfinder;

import android.graphics.Bitmap;
import android.net.Uri;

public class DataModel {
    private Bitmap bitmap;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    private Uri uri;

    public DataModel(Bitmap bitmap, boolean isChecked,Uri uri) {
        this.bitmap = bitmap;
        this.isChecked = isChecked;
        this.uri = uri;
    }

    private boolean isChecked;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
