package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 2. 18..
 */

public class    StackViewAdapter extends StackAdapter<PlayerModel> {

    static int[] colors;
    private RecyclerViewClickListener listener;

    /*-------------------------------------------------------------------*/


    public void setRecyclerViewClickListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }


    /*-------------------------------------------------------------------*/

    public StackViewAdapter(Context context, boolean isHome) {
        super(context);

        if ( isHome ) {
            colors = getContext().getResources().getIntArray(R.array.home_team_colors);
        } else {
            colors = getContext().getResources().getIntArray(R.array.away_team_colors);
        }
    }

    @Override
    public void bindView(PlayerModel data, int position, CardStackView.ViewHolder holder) {

        if ( holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;

            h.llPlayerComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null ) {
                        listener.onViewClicked(v, position);
                    }
                }
            });

            /**
             * 저장하기 리스너
             */
            h.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null ) {
                        listener.onViewClicked(v, position);
                    }
                }
            });

            /**
             * 선수평점 리스너
             */
            h.rbPlayer.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    h.tvMyRating.setText(String.valueOf((rating * 2)));
                    data.setRating(rating * 2);
                }
            });

            h.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float rating = (float)progress / 10;
                    h.tvMyRating.setText(String.valueOf(((float) progress / 10)));
                    h.tvMyRating.setBackgroundColor(ColorUtils.getRatingBackgroundColor(getContext(), rating));
                    data.setRating(rating);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            /**
             * 선수코멘트 입력 리스너
             */
            h.etComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    data.setPlayerComment(s.toString());
                }
            });

            /**
             * 데이터 바인딩
             */
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {

        View view;

        view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    /*---------------------------------------------------------------------*/

    static class ColorItemViewHolder extends CardStackView.ViewHolder {

        View mLayout;
        View mContainerContent;
        TextView tvIsSave;
        ImageView ivPlayerAvatar;
        TextView tvPlayerName;
        ImageView ivGoal;
        ImageView ivYellowCard;
        ImageView ivRedCard;
        ImageView ivSubstitutions_on;
        ImageView ivSubstitutions_off;
        TextView tvSubsMinute;
        RatingBar rbPlayer;
        EditText etComment;
        Button btnSave;
        TextView tvRatingAvgValue;
        TextView tvRatingCount;
        TextView tvMyRating;
        LinearLayout llPlayerComment;
        TextView tvPlayerCommentCount;
        SeekBar seekBar;


        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            tvIsSave = view.findViewById(R.id.tv_is_save);
            ivPlayerAvatar = view.findViewById(R.id.iv_player);
            tvPlayerName = view.findViewById(R.id.tv_player_name);
            ivGoal = view.findViewById(R.id.iv_goal);
            ivYellowCard = view.findViewById(R.id.iv_yellow_card);
            ivRedCard = view.findViewById(R.id.iv_red_card);
            ivSubstitutions_on = view.findViewById(R.id.iv_player_substitutions_on);
            ivSubstitutions_off = view.findViewById(R.id.iv_player_substitutions_off);
            tvSubsMinute = view.findViewById(R.id.tv_player_subs_minute);
            etComment = view.findViewById(R.id.et_player_comment);
            btnSave = view.findViewById(R.id.btn_save);
            rbPlayer = view.findViewById(R.id.rb_player_rating);
            tvRatingAvgValue  = view.findViewById(R.id.tv_player_rating);
            tvRatingCount = view.findViewById(R.id.tv_rating_count);
            tvMyRating = view.findViewById(R.id.tv_my_rating);
            llPlayerComment = view.findViewById(R.id.ll_player_comment);
            tvPlayerCommentCount = view.findViewById(R.id.tv_player_comment_count);
            seekBar = view.findViewById(R.id.seekBar);

        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(PlayerModel data, int position) {

            //mLayout.getBackground().setColorFilter(colors[position % colors.length], PorterDuff.Mode.SRC_IN);

            tvPlayerName.setText(data.getBackNumber() + ". " + data.getPlayerName() + "  " + data.getPos());
            tvRatingAvgValue.setText(data.getRatingString());
            tvRatingAvgValue.setBackgroundColor(ColorUtils.getRatingBackgroundColor(getContext(), data.getRating()));
            tvRatingCount.setText("("+data.getRatingCountString()+")");
            tvPlayerCommentCount.setText(data.getCommentCountString());

            if (data.getStats().hasGoals()) {
                ivGoal.setVisibility(View.VISIBLE);
            } else {
                ivGoal.setVisibility(View.GONE);
            }

            if ( data.getStats().hasYellowCards() ) {
                ivYellowCard.setVisibility(View.VISIBLE);
            } else {
                ivYellowCard.setVisibility(View.GONE);
            }

            if ( data.getStats().hasRedCards()) {
                ivRedCard.setVisibility(View.VISIBLE);
            } else {
                ivRedCard.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(data.getSubstitutions())) {

                if ( data.getSubstitutions().equals("on") ) {
                    ivSubstitutions_on.setVisibility(View.VISIBLE);
                    ivSubstitutions_off.setVisibility(View.GONE);
                } else if ( data.getSubstitutions().equals("off")) {
                    ivSubstitutions_on.setVisibility(View.GONE);
                    ivSubstitutions_off.setVisibility(View.VISIBLE);
                } else {
                    ivSubstitutions_on.setVisibility(View.GONE);
                    ivSubstitutions_off.setVisibility(View.GONE);
                }

                tvSubsMinute.setVisibility(View.VISIBLE);
                tvSubsMinute.setText(data.getSubsMinute());

            } else {

                ivSubstitutions_on.setVisibility(View.GONE);
                ivSubstitutions_off.setVisibility(View.GONE);
                tvSubsMinute.setVisibility(View.GONE);
                tvSubsMinute.setText("");
            }

            /*-----------------------------------------------------------*/

            GlideApp.with(getContext())
                    .load(data.getPlayerLargeImageUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_person)
                    .into(ivPlayerAvatar);
        }

        private Drawable getSubsitutionsImageResource(String substitution) {

            if (substitution != null && substitution.equals("off")) {
                return DrawableHelper.withContext(getContext())
                        .withDrawable(R.drawable.ic_substitution_out)
                        .tint().get();

            } else if (substitution != null && substitution.equals("on")) {
                return DrawableHelper.withContext(getContext())
                        .withDrawable(R.drawable.ic_substitution_in)
                        .tint().get();
            } else {
                return null;
            }
        }
    }

    /*---------------------------------------------------------------------*/

}
