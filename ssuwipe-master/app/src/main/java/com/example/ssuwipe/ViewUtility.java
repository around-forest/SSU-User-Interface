package com.example.ssuwipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewUtility {

    public static void initToolbar(LinearLayout toolbarLayout, Activity activity, String titleText, boolean isInfo) {
        TextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView backBtn = toolbarLayout.findViewById(R.id.toolbar_back_btn);
        ImageView infoBtn = toolbarLayout.findViewById(R.id.toolbar_info_btn);
        title.setText(titleText);
        backBtn.setOnClickListener(view -> activity.finish());
        infoBtn.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MyInfoActivity.class);
            activity.startActivity(intent);
        });
        if(isInfo) infoBtn.setVisibility(View.INVISIBLE);
    }

    public static void setImageFromUrl(Context ctx, ImageView imageView, String urlString, boolean blur) {
        List<Transformation<Bitmap>> transforms = new LinkedList<>();
        transforms.add(new CenterCrop());
        if (blur) transforms.add(new BlurTransformation());
        MultiTransformation<Bitmap> transformation = new MultiTransformation(transforms);

        if(blur) Glide.with(ctx).load(urlString).error(R.mipmap.ic_launcher).fallback(R.mipmap.ic_launcher).apply(RequestOptions.bitmapTransform(transformation)).into(imageView);
        else Glide.with(ctx).load(urlString).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).fallback(R.mipmap.ic_launcher).centerCrop().into(imageView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setRestaurantInfoLayout(Activity activity, LinearLayout layout, Restaurant restaurant) {
        TextView restaurantName = layout.findViewById(R.id.restaurantName);
        TextView restaurantType = layout.findViewById(R.id.restaurantType);
        ImageView restaurantSave = layout.findViewById(R.id.restaurantSave);
        TextView restaurantRating = layout.findViewById(R.id.restaurantRating);
        TextView restaurantLocation = layout.findViewById(R.id.restaurantLocation);
        ImageView locationCopy = layout.findViewById(R.id.locationCopy);

        LinearLayout shareBtn = layout.findViewById(R.id.shareButton);
        LinearLayout mapBtn = layout.findViewById(R.id.mapButton);
        LinearLayout blockBtn = layout.findViewById(R.id.blockButton);
        TextView blockText = layout.findViewById(R.id.blockText);

        ImageView image1 = layout.findViewById(R.id.restaurantPhoto1);
        ImageView image21 = layout.findViewById(R.id.restaurantPhoto2_1);
        ImageView image22 = layout.findViewById(R.id.restaurantPhoto2_2);
        ImageView image31 = layout.findViewById(R.id.restaurantPhoto3_1);
        ImageView image32 = layout.findViewById(R.id.restaurantPhoto3_2);
        ImageView image41 = layout.findViewById(R.id.restaurantPhoto4_1);
        ImageView image42 = layout.findViewById(R.id.restaurantPhoto4_2);
        ImageView image5 = layout.findViewById(R.id.restaurantPhoto5);

        RecyclerView recyclerView = layout.findViewById(R.id.restaurantMenu);
        MenuListRecyclerAdapter adapter = new MenuListRecyclerAdapter(activity.getApplicationContext(), restaurant.getMenu());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        restaurantName.setText(restaurant.getName());
        restaurantType.setText(restaurant.getType());
        restaurantRating.setText(restaurant.getRatingString());
        restaurantLocation.setText(restaurant.getLocation());

        restaurantName.requestLayout();
        restaurantType.requestLayout();
        restaurantLocation.requestLayout();
        locationCopy.requestLayout();

        shareBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_TEXT, "[네이버 지도]\n" + restaurant.getName() + "\n" + restaurant.getLocation() + "\nhttps://naver.me/" + restaurant.getShare());
            intent.setType("text/plain");
            activity.startActivity(Intent.createChooser(intent, "앱을 선택해 주세요"));
        });

        mapBtn.setOnClickListener(view -> {
            double lat = restaurant.getLat(), lng = restaurant.getLng();
            String uri = "nmap://route/public?dlat=" + lat + "&dlng=" + lng + "&dname=" + restaurant.getLocation() + "&appname=com.example.ssuwipe";
            Intent intent = new Intent();
            intent.setData(Uri.parse(uri));
            activity.startActivity(intent);
        });

        blockText.setText(ServerAPI.getBan(restaurant.getId()) ? "차단 해제" : "차단");

        blockBtn.setOnClickListener(view -> {
            if(ServerAPI.getBan(restaurant.getId())){
                ServerAPI.setBan(restaurant.getId(), false);
                blockText.setText("차단");
            }
            else{
                ServerAPI.setBan(restaurant.getId(), true);
                blockText.setText("차단 해제");
                Toast.makeText(activity, "다음부터 추천하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        if(ServerAPI.getSave(restaurant.getId())){
            restaurantSave.setImageDrawable(activity.getDrawable(R.drawable.ic_favorite));
        }
        else{
            restaurantSave.setImageDrawable(activity.getDrawable(R.drawable.ic_favorite_border));
        }

        restaurantSave.setOnClickListener(view -> {
            if(ServerAPI.getSave(restaurant.getId())) {
                ServerAPI.setSave(restaurant.getId(), false);
                restaurantSave.setImageDrawable(activity.getDrawable(R.drawable.ic_favorite_border));
            }
            else {
                ServerAPI.setSave(restaurant.getId(), true);
                restaurantSave.setImageDrawable(activity.getDrawable(R.drawable.ic_favorite));
            }
        });

        locationCopy.setImageDrawable(activity.getDrawable(R.drawable.button_copy));
        locationCopy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", restaurant.getLocation());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity, "복사되었습니다.", Toast.LENGTH_SHORT).show();
        });

        setImageFromUrl(activity, image1, restaurant.getPhoto().get(0), false);
        setImageFromUrl(activity, image21, restaurant.getPhoto().get(1), false);
        setImageFromUrl(activity, image22, restaurant.getPhoto().get(2), false);
        setImageFromUrl(activity, image31, restaurant.getPhoto().get(3), false);
        setImageFromUrl(activity, image32, restaurant.getPhoto().get(4), false);
        setImageFromUrl(activity, image41, restaurant.getPhoto().get(5), false);
        setImageFromUrl(activity, image42, restaurant.getPhoto().get(6), false);
        setImageFromUrl(activity, image5, restaurant.getPhoto().get(7), false);
    }
}
