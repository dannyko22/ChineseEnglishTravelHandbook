package com.chineseenglishtravelhandbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Danny on 28/08/2016.
 */

public class CategoryAdapterClass extends RecyclerView.Adapter<CategoryAdapterClass.ViewHolder>  {

    Context context;
    ArrayList<TravelCategoryData> travelCategoryData;


    public CategoryAdapterClass(Context _context, ArrayList<TravelCategoryData> _travelCategoryData) {

        this.context = _context;
        this.travelCategoryData = _travelCategoryData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.myCategory.setText(travelCategoryData.get(position).getCategory());
        Picasso.with(context).load(travelCategoryData.get(position).getFilename()).into(holder.myImage);
    }


    @Override
    public int getItemCount() {
        return travelCategoryData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView myCategory;
        public final ImageView myImage;



        public ViewHolder(View itemView) {
            super(itemView);
            myCategory = (TextView)itemView.findViewById(R.id.categoryTextView);
            myImage = (ImageView)itemView.findViewById(R.id.categoryImageView);
        }

    }

}
