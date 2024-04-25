package com.example.ssuwipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final List<Restaurant> cards;
    private final Context ctx;
    CardAdapter(Context ctx, List<Restaurant> cards){ super(); this.ctx = ctx; this.cards = cards; }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private static OnItemClickListener mClickListener = null;

    void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(ctx, cards.get(position));
    }

    @Override
    public int getItemCount() { return cards.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView backgroundImage;
        ViewHolder(View view) {
            super(view);
            this.view = view;
            backgroundImage = view.findViewById(R.id.card_image);

            view.setOnClickListener(view1 -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION && mClickListener != null) mClickListener.onItemClick(view1, pos);
            });
        }

        void onBind(Context ctx, Restaurant data) {
            ViewUtility.setImageFromUrl(ctx, backgroundImage, data.getPhotoUrl(), false);
        }
    }
}
