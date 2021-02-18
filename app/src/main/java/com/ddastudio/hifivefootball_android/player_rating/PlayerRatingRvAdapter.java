package com.ddastudio.hifivefootball_android.player_rating;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by hongmac on 2017. 11. 9..
 */

public class PlayerRatingRvAdapter extends RecyclerView.Adapter<PlayerRatingRvAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private List<PlayerRatingInfoModel> mList;
    private int scrollDirection = 0;

    public PlayerRatingRvAdapter() {
        this.mList = new ArrayList<>();
    }

    public PlayerRatingRvAdapter(List<PlayerRatingInfoModel> data) {
        this.mList = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.abc_item_city_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int iconTint = ContextCompat.getColor(holder.itemView.getContext(), R.color.grayIconTint);
        //Forecast forecast = data.get(position);
        PlayerRatingInfoModel lineupData = mList.get(position);

        int backgroundInt = CommonUtils.randomBetween(1, 10);
        GlideApp.with(holder.itemView.getContext())
                .load("https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/stadium/background_stadium_" + backgroundInt+ ".jpg")
                //    .load(R.drawable.ic_person)
                    //.bitmapTransform(new BlurTransformation(holder.itemView.getContext(), 15))
                .transform(new BlurTransformation(15))
                .into(holder.backgroundStadiumImageView);

        holder.textView.setText(lineupData.getPlayerName());

        Drawable upDrawable = DrawableHelper.withContext(holder.itemView.getContext())
                .withColor(R.color.md_grey_400)
                .withDrawable(R.drawable.ic_thumb_up_vector)
                .tint().get();

        Drawable downDrawable = DrawableHelper.withContext(holder.itemView.getContext())
                .withColor(R.color.md_grey_400)
                .withDrawable(R.drawable.ic_thumb_down_vector)
                .tint().get();

        if ( lineupData.getUserUpNDonw() == 0 ) {
            holder.ratingUpButton.setImageDrawable(upDrawable);
            holder.ratingDownButton.setImageDrawable(downDrawable);
        } else if ( lineupData.getUserUpNDonw() == 1) {
            upDrawable = DrawableHelper.withContext(holder.itemView.getContext())
                    .withColor(R.color.md_blue_100)
                    .withDrawable(R.drawable.ic_thumb_up_vector)
                    .tint().get();
            holder.ratingUpButton.setImageDrawable(upDrawable);
            holder.ratingDownButton.setImageDrawable(downDrawable);
        } else if ( lineupData.getUserUpNDonw() == -1 ) {
            downDrawable = DrawableHelper.withContext(holder.itemView.getContext())
                    .withColor(R.color.md_red_100)
                    .withDrawable(R.drawable.ic_thumb_down_vector)
                    .tint().get();
            holder.ratingUpButton.setImageDrawable(upDrawable);
            holder.ratingDownButton.setImageDrawable(downDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public PlayerRatingInfoModel getItem(int position) {

        return mList.get(position);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void setItems(List<PlayerRatingInfoModel> items) {
        mList = items;
        notifyDataSetChanged();
    }

    /*---------------------------------------------------------------------------------------------*/

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView backgroundStadiumImageView;
        private ImageView imageView;
        private TextView textView;

        private ImageButton ratingUpButton;
        private ImageButton ratingDownButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.player_image);
            backgroundStadiumImageView = (ImageView) itemView.findViewById(R.id.background_stadium);
            textView = (TextView) itemView.findViewById(R.id.city_name);

            ratingUpButton = itemView.findViewById(R.id.player_rating_up);
            ratingDownButton = itemView.findViewById(R.id.player_rating_down);

            itemView.findViewById(R.id.container2).setOnClickListener(this);

            ratingUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if ( mList.get(position).getUserUpNDonw() == 1) {
                        mList.get(position).setUserUpNDonw(0);
                    } else {
                        mList.get(position).setUserUpNDonw(1);
                    }

                    notifyItemChanged(position);

                    if ( position + 1 == mList.size()) {
                        scrollDirection = -1;
                    } else if ( position == 0 ) {
                        scrollDirection = 1;
                    }

                    if ( mList.size() > 1 ) {
                        parentRecycler.smoothScrollToPosition(position + scrollDirection);
                    }
                }
            });

            ratingDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if ( mList.get(position).getUserUpNDonw() == -1) {
                        mList.get(position).setUserUpNDonw(0);
                    } else {
                        mList.get(position).setUserUpNDonw(-1);
                    }

                    notifyItemChanged(position);

                    if ( position + 1 == mList.size()) {
                        scrollDirection = -1;
                    } else if ( position == 0 ) {
                        scrollDirection = 1;
                    }

                    if ( mList.size() > 1 ) {
                        parentRecycler.smoothScrollToPosition(position + scrollDirection);
                    }
                }
            });
        }

        public void showText() {
            int parentHeight = ((View) imageView.getParent()).getHeight();
            float scale = (parentHeight - textView.getHeight()) / (float) imageView.getHeight();
            //imageView.setPivotX(imageView.getWidth() * 0.5f);
            //imageView.setPivotY(0);
            imageView.animate().scaleX(scale)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            textView.setVisibility(View.VISIBLE);
                            //imageView.setColorFilter(R.color.md_grey_300);
                        }
                    })
                    .scaleY(scale).setDuration(200)
                    .start();
        }

        public void hideText() {
            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.md_grey_600));
            textView.setVisibility(View.INVISIBLE);
            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();
        }

        @Override
        public void onClick(View v) {
            parentRecycler.smoothScrollToPosition(getAdapterPosition());
        }
    }

//    private static class TintOnLoad implements RequestListener<Integer, GlideDrawable> {
//
//        private ImageView imageView;
//        private int tintColor;
//
//        public TintOnLoad(ImageView view, int tintColor) {
//            this.imageView = view;
//            this.tintColor = tintColor;
//        }
//
//        @Override
//        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
//            return false;
//        }
//
//        @Override
//        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//            imageView.setColorFilter(tintColor);
//            return false;
//        }
//    }
}
