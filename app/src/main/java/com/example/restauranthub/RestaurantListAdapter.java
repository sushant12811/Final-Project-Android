package com.example.restauranthub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>  {
    Context context;
    ArrayList<RestaurantModel>  restaurantList;

    ListenerInterface listenerInterface;
    String maroon = "#9E3434";
    int moronColor = Color.parseColor(maroon);

   final boolean isFavoritesListing;



    public RestaurantListAdapter(Context context, ArrayList<RestaurantModel> restaurantList, ListenerInterface listenerInterface, boolean isFavoritesListing) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.listenerInterface = listenerInterface;
        this.isFavoritesListing = isFavoritesListing;
    }



    @NonNull
    @Override
    public RestaurantListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_restaurant_list, parent, false);
        return new ViewHolder(view, listenerInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListAdapter.ViewHolder holder, int position) {
        RestaurantModel model = restaurantList.get(position);
        holder.restaurantName.setText(model.getRestaurantName());
        holder.restaurantLocation.setText(model.getRestaurantLocation());
        if (model.getRestaurantImage() != null && !model.getRestaurantImage().isEmpty()) {
            Glide.with(context)
                    .load(model.getRestaurantImage())
                    .placeholder(R.drawable.bull)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.restaurantImage);
        } else if (model.getRestaurantImageResourceId() != 0) {
            holder.restaurantImage.setImageResource(model.getRestaurantImageResourceId());
        } else {
            holder.restaurantImage.setImageResource(R.drawable.img);
        }

        if (isFavoritesListing) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
            if (model.isFavorite()) {
                holder.favoriteIcon.setColorFilter(moronColor);
            } else {
                holder.favoriteIcon.setColorFilter(Color.GRAY);
            }
        } else {
            holder.favoriteIcon.setVisibility(View.GONE);
        }

        holder.favoriteIcon.setOnClickListener(v -> {
            if (listenerInterface != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listenerInterface.onRemove(pos);
                }
            }
        });



    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFiltered(ArrayList<RestaurantModel> filteredList) {
        this.restaurantList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void adapterFilterList(ArrayList<RestaurantModel> adapterFilteredList){
        restaurantList = adapterFilteredList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView restaurantName, restaurantLocation;
       ImageView restaurantImage, favoriteIcon;

        public ViewHolder(@NonNull View itemView, ListenerInterface listenerInterface) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantLocation = itemView.findViewById(R.id.locationId);
            restaurantImage = itemView.findViewById(R.id.restaurantImage);
            favoriteIcon = itemView.findViewById(R.id.favourite_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listenerInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            listenerInterface.onClick(pos);
                        }
                    }


                }
            });


        }


    }


}
