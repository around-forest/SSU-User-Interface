package com.example.ssuwipe;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuListRecyclerAdapter extends RecyclerView.Adapter<MenuListRecyclerAdapter.ItemViewHolder> {
    private final ArrayList<RestaurantMenu> listData;
    private final Context ctx;

    MenuListRecyclerAdapter(Context ctx, ArrayList<RestaurantMenu> listData){ super(); this.ctx = ctx; this.listData = listData; }


    @NonNull
    @Override
    public MenuListRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuListRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuListRecyclerAdapter.ItemViewHolder holder, int position) {
        holder.onBind(ctx, listData.get(position));
    }

    @Override
    public int getItemCount() { return listData.size(); }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView menu_photo;
        private final TextView menu_name;
        private final TextView menu_content;
        private final TextView menu_price;

        ItemViewHolder(View itemView) {
            super(itemView);

            menu_price = itemView.findViewById(R.id.menu_price);
            menu_photo = itemView.findViewById(R.id.menu_photo);
            menu_name = itemView.findViewById(R.id.menu_name);
            menu_content = itemView.findViewById(R.id.menu_content);

            menu_name.setSingleLine(true);
            menu_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            menu_name.setSelected(true);
        }

        void onBind(Context ctx, RestaurantMenu data) {
            ViewUtility.setImageFromUrl(ctx, menu_photo, data.getPhoto(), false);
            menu_price.setText(data.getPrice());
            menu_name.setText(data.getName());
            menu_content.setText(data.getContent());
        }
    }
}