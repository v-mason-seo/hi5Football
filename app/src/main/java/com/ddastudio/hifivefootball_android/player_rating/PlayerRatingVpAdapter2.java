package com.ddastudio.hifivefootball_android.player_rating;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by hongmac on 2018. 2. 9..
 */

public class PlayerRatingVpAdapter2 extends PagerAdapter {

    OnSaveClickListener mOnSaveClickListener;

    public interface OnSaveClickListener {
        void onSave(View view, int position);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.mOnSaveClickListener = listener;
    }

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private boolean mIsTwoWay;
    private List<PlayerRatingInfoModel> lineupList;

    public PlayerRatingVpAdapter2(final Context context, final boolean isTwoWay, List<PlayerRatingInfoModel> lineupList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIsTwoWay = isTwoWay;
        this.lineupList = lineupList;
    }

    @Override
    public int getCount() {
        return lineupList.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    public PlayerRatingInfoModel getItem(int position) {

        return lineupList.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.row_player_rating_card_item, container, false);

        final Button btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSaveClickListener.onSave(v, position);
            }
        });

        bindData(view, lineupList.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    private void bindData(View view, PlayerRatingInfoModel lineup) {

        final TextView tvPlayerName = view.findViewById(R.id.tv_player_name);
        final ImageView ivPlayerAvatar = view.findViewById(R.id.iv_player_avatar);
        final ImageView ivEmblem = view.findViewById(R.id.iv_emblem);
        final EditText etComment = view.findViewById(R.id.et_player_comment);
        final TextView tvRating = view.findViewById(R.id.tv_rating);
        final RatingBar rbPlayer = view.findViewById(R.id.rb_player_rating);
        final Button btnSave = view.findViewById(R.id.btn_save);

        final ImageView ivGoal = view.findViewById(R.id.iv_goal);
        final ImageView ivYellowCard = view.findViewById(R.id.iv_yellow_card);
        final ImageView ivRedCard = view.findViewById(R.id.iv_red_card);
        final ImageView ivSubstitutions = view.findViewById(R.id.iv_player_substitutions);
        final TextView tvSubsMinute = view.findViewById(R.id.tv_player_subs_minute);


        if (lineup.hasGoals()) {
            ivGoal.setVisibility(View.VISIBLE);
        } else {
            ivGoal.setVisibility(View.GONE);
        }

        if ( lineup.hasYellowCards() ) {
            ivYellowCard.setVisibility(View.VISIBLE);
        } else {
            ivYellowCard.setVisibility(View.GONE);
        }

        if ( lineup.hasRedCards()) {
            ivRedCard.setVisibility(View.VISIBLE);
        } else {
            ivRedCard.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(lineup.getSubstitutions())) {
            ivSubstitutions.setVisibility(View.VISIBLE);
            tvSubsMinute.setVisibility(View.VISIBLE);
            tvSubsMinute.setText(lineup.getSubsMinute());
        } else {
            ivSubstitutions.setVisibility(View.GONE);
            tvSubsMinute.setVisibility(View.GONE);
            tvSubsMinute.setText("");
        }

        tvPlayerName.setText(lineup.getPlayerName());
        etComment.setText(lineup.getPlayerComment());
        tvRating.setText(lineup.getPlayerAvgRatingAndCountString());
        rbPlayer.setRating(lineup.getPlayerRating() / 2);

        if ( lineup.getContentId() != 0 ) {
            btnSave.setText("수정");
        } else  {
            btnSave.setText("저장");
        }

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lineup.setPlayerComment(s.toString());
            }
        });

        rbPlayer.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if ( fromUser == true ) {
                    //tvRating.setText(String.valueOf(rating));
                    lineup.setPlayerRating(rating * 2);
                }
            }
        });

//        Glide.with(mContext)
//                .load(lineup.getPlayer().getPlayerLargeImageUrl())
//                .asBitmap()
//                .centerCrop()
//                .transform(new CropCircleTransformation(mContext))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.ic_person)
//                .into(ivPlayerAvatar);
//
//        Glide.with(mContext)
//                .load(lineup.getPlayer().getEmblemUrl())
//                .asBitmap()
//                .centerCrop()
//                .transform(new CropCircleTransformation(mContext))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.ic_person)
//                .into(ivEmblem);

    }
}
