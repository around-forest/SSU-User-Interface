package com.example.ssuwipe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;

public class SsuwipeActivity extends AppCompatActivity implements CardStackListener {

    CardStackView cardStackView;
    ArrayList<Restaurant> restaurantList;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssuwipe);

        ViewUtility.initToolbar(findViewById(R.id.ssuwipe_toolbar), this, "Ssuwipe", false);
        initCardStackView();
    }

    void initCardStackView() {
        cardStackView = findViewById(R.id.ssuwipe_card_stack_view);
        restaurantList = ServerAPI.getRestaurantBySuggestionPolicy();

        CardAdapter adapter = new CardAdapter(this, restaurantList);
        adapter.setOnItemClickListener((view, position) -> {
            SlidingUpPanelLayout layout = findViewById(R.id.sliding_layout);
            layout.setPanelState(PanelState.EXPANDED);
        });
        cardStackView.setAdapter(adapter);
        index = 0;

        CardStackLayoutManager manager = new CardStackLayoutManager(this, this);
        manager.setStackFrom(StackFrom.Top);
        manager.setCanScrollVertical(false);
        manager.setMaxDegree(0);
        SwipeAnimationSetting autoSwipeSetting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Fast.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        manager.setSwipeAnimationSetting(autoSwipeSetting);
        cardStackView.setLayoutManager(manager);

        ViewUtility.setRestaurantInfoLayout(this, findViewById(R.id.ssuwipe_info_layout), restaurantList.get(index));
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {}

    private String directionToString(Direction direction) {
        if(direction == Direction.Left) return "left";
        if(direction == Direction.Right) return "right";
        if(direction == Direction.Top) return "top";
        if(direction == Direction.Bottom) return "bottom";
        return "";
    }

    @Override
    public void onCardSwiped(Direction direction) {
        Restaurant restaurant = restaurantList.get(index);
        ServerAPI.sendSwipeLog(restaurant.getId(), directionToString(direction));
    }

    @Override
    public void onCardRewound() {}

    @Override
    public void onCardCanceled() {}

    @Override
    public void onCardAppeared(View view, int position) {
        index = position;
        ViewUtility.setRestaurantInfoLayout(this, findViewById(R.id.ssuwipe_info_layout), restaurantList.get(index));
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        if(position + 1 == restaurantList.size()) {
            finish();
        }
    }
}