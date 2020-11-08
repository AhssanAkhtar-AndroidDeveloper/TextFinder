package com.example.textfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomHolder> {

    Context context;
    List<DataModel> list;
    GetClick getClick;
    CheckBox checkBoxAll;

    public CustomAdapter(Context context, final List<DataModel> list, GetClick getClick, CheckBox checkBoxAll) {
        this.context = context;
        this.list = list;
        this.getClick = getClick;
        this.checkBoxAll = checkBoxAll;

        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setChecked(b);
                }
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomHolder(LayoutInflater.from(context).inflate(R.layout.custom_data_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, final int position) {
        try {
            holder.imageView.setImageBitmap(list.get(position).getBitmap());
            holder.checkBox.setChecked(list.get(position).isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(position).setChecked(b);
                    int sum = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isChecked()) {
                            sum += 1;
                        }
                    }
                    getClick.getClick(sum);
                }
            });
        } catch (Exception e) {
            Log.d("error_", e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ImgCustom);
            checkBox = itemView.findViewById(R.id.chkBox);
        }
    }
}
