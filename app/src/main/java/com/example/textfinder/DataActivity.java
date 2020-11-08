package com.example.textfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.kaopiz.kprogresshud.KProgressHUD;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String data = "";
    private KProgressHUD progressHUD;
    List<DataModel> listFiltered;
    List<Uri> listAllUri;
    private String searchText = "";
    private CheckBox checkBoxAll;
    private TextView tvDelete, tvCancel, tvSelected, tvSearch, tvSearchLive, TvResult;
    private CustomAdapter customAdapter = null;
    int j = 0;
    MyTask myTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        getSupportActionBar().hide();
        searchText = getIntent().getExtras().getString("data");
        listFiltered = new ArrayList<>();
        listAllUri = new ArrayList<>();
        init();
        setListener();
        initLoader();
        myTask = new MyTask();
        myTask.execute();
    }

    private void setListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTask.cancel(true);
                finish();
            }
        });


        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = listFiltered.size() - 1; i >= 0; i--) {
                    if (listFiltered.get(i).isChecked()) {
                        Log.d("deleted_files", listFiltered.size() + " " + i);
                        try {
                            File file = new File(getFilePath(listFiltered.get(i).getUri()));
                            if (file.exists()) {
                                if (file.delete()) {
                                    listFiltered.remove(i);
                                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    scanIntent.setData(Uri.fromFile(file));
                                    sendBroadcast(scanIntent);
                                    if (customAdapter != null)
                                        customAdapter.notifyDataSetChanged();
                                } else
                                    Log.d("log_under", "no deleted");
                            } else
                                Log.d("log_under", "not exist");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        myTask.cancel(true);
        finish();
    }

    private void setRecycle() {
        try {
            if (listFiltered.isEmpty()){
                TvResult.setText("Not Matched!");
            }else{
                TvResult.setText("");
            }
            Log.d("list_size", listFiltered.size() + "");
            customAdapter = new CustomAdapter(getApplicationContext(), listFiltered, new GetClick() {
                @Override
                public void getClick(int total) {
                    tvSelected.setText(total + " selected");
                }
            }, checkBoxAll);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(customAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        recyclerView = findViewById(R.id.recycle);
        checkBoxAll = findViewById(R.id.chkBox);
        tvCancel = findViewById(R.id.tvCancel);
        tvDelete = findViewById(R.id.tvDelete);
        tvSelected = findViewById(R.id.totalSelected);
        tvSearch = findViewById(R.id.totalSearch);
        tvSearchLive = findViewById(R.id.totalSearchLive);
        TvResult = findViewById(R.id.TvResult);
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            TvResult.setVisibility(View.VISIBLE);
            TvResult.setText("Scanning...");

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRecycle();
            progressHUD.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getAllImagesFromGallery();
            return null;

        }
    }

    private void initLoader() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void getAllImagesFromGallery() {
        listFiltered.clear();
        listAllUri.clear();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (myTask.isCancelled()) {
                        break;
                    }
                    long imageID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                    Uri uri1 = Uri.withAppendedPath(uri, imageID + "");
                    listAllUri.add(uri1);
                } while (cursor.moveToNext());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvSearch.setText("" + listAllUri.size());
//                    }
//                });

                // scanning

                for (j = 0; j < listAllUri.size(); j++) {
                    if (myTask.isCancelled()) {
                        break;
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            tvSearch.setText("" + listAllUri.size());
                            tvSearchLive.setText("" + (j + 1));
                        }
                    });

//                        DataActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run()
//                            {
//
//
//
//                            }
//                        });

                    TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!txtRecognizer.isOperational()) {
                    } else {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), listAllUri.get(j));
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray items = txtRecognizer.detect(frame);
                            boolean isTextExist = false;
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = (TextBlock) items.valueAt(i);
                                Log.d("live_log_", item.getValue());
                                if (item.getValue().toLowerCase().contains(searchText.toLowerCase())) {
                                    isTextExist = true;
                                }
                            }
                            if (isTextExist) {
                                listFiltered.add(new DataModel(bitmap, false, listAllUri.get(j)));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            } else {
                Toast.makeText(this, "No Record", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTask.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myTask.cancel(true);
    }
}
