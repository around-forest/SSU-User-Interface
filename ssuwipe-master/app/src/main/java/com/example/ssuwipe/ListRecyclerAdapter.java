package com.example.ssuwipe;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ItemViewHolder> {
    private final ArrayList<Restaurant> listData;
    private final Context ctx;

    ListRecyclerAdapter(Context ctx, ArrayList<Restaurant> listData){ super(); this.ctx = ctx; this.listData = listData; }

    interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    private static OnItemClickListener mListener;

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(ctx, listData.get(position));
    }

    @Override
    public int getItemCount() { return listData.size(); }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photo;
        private final TextView type;
        private final TextView name;
        private final TextView rating;
        private final TextView location;
        private final ImageView saveBtn;
        private final ImageView copyBtn;

        ItemViewHolder(View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.photo);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);

            name.setSingleLine(true);
            name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            name.setSelected(true);
            rating = itemView.findViewById(R.id.rating);
            location = itemView.findViewById(R.id.location);

            location.setSingleLine(true);
            location.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            location.setSelected(true);

            saveBtn = itemView.findViewById(R.id.item_save);
            copyBtn = itemView.findViewById(R.id.item_copy);

            itemView.setOnClickListener (view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) mListener.onItemClick (view,position);
            });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        void onBind(Context ctx, Restaurant data) {
            ViewUtility.setImageFromUrl(ctx, photo, data.getPhotoUrl(), false);
            type.setText(data.getType());
            name.setText(data.getName());
            rating.setText(data.getRatingString());
            location.setText(data.getLocation());

            if(ServerAPI.getSave(data.getId())){
                saveBtn.setImageDrawable(ctx.getDrawable(R.drawable.ic_favorite));
            }
            else{
                saveBtn.setImageDrawable(ctx.getDrawable(R.drawable.ic_favorite_border));
            }

            saveBtn.setOnClickListener(view -> {
                if(ServerAPI.getSave(data.getId())) {
                    ServerAPI.setSave(data.getId(), false);
                    saveBtn.setImageDrawable(ctx.getDrawable(R.drawable.ic_favorite_border));
                }
                else {
                    ServerAPI.setSave(data.getId(), true);
                    saveBtn.setImageDrawable(ctx.getDrawable(R.drawable.ic_favorite));
                }
            });

            copyBtn.setImageDrawable(ctx.getDrawable(R.drawable.button_copy));
            copyBtn.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", data.getLocation());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ctx, "복사되었습니다.", Toast.LENGTH_SHORT).show();
            });
        }
    }
}