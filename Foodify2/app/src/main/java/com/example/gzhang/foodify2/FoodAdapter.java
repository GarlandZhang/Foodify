package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by GZhang on 2017-12-31.
 */

public class FoodAdapter extends BaseAdapter {

    private Context context;
    List<FoodItem> foods;

    public FoodAdapter(Activity context, List<FoodItem> foods){
        super();
        this.context = context;
        this.foods = foods;
    }


    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder{
        ImageView deleteButtonIV;
        TextView foodTV;
        TextView ExpiryDateTV;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Holder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.row_layout, null);
            holder = new Holder();
            holder.deleteButtonIV = (ImageView) convertView.findViewById(R.id.deleteImageView);
            holder.foodTV = (TextView) convertView.findViewById(R.id.foodNameTextView);
            holder.ExpiryDateTV = (TextView) convertView.findViewById(R.id.foodExpiryDateTextView);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }

        holder.deleteButtonIV.setImageResource(R.drawable.delete);
        holder.foodTV.setText(foods.get(position).getName());
        holder.ExpiryDateTV.setText(foods.get(position).getExpiryDate());

        return convertView;
    }
}
