package com.ddastudio.hifivefootball_android.football_chat;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.widget.MaterialBadgeTextView;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FootballChatRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<ChatAndAttributeModel> mItems;
    TextDrawable mDrawable;

    public FootballChatRvAdapter(Context context) {
        this.mContext = context;
        mItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new FootballChatViewHolder(inflater.inflate(R.layout.row_football_chat_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatAndAttributeModel chatItem = (ChatAndAttributeModel)mItems.get(position);
        FootballChatViewHolder footballChatViewHolder = (FootballChatViewHolder)viewHolder;
        footballChatViewHolder.bindData(chatItem);
        footballChatViewHolder.llEmblemContainer.setVisibility(View.INVISIBLE);
        footballChatViewHolder.ivMain.setVisibility(View.INVISIBLE);

        if ( chatItem.getChat() != null ) {
            // 대표이미지를 가져온다.
            List<String> urls = chatItem.getChat().getImageUrl();

            // 대표 이미지
            if ( urls != null && urls.size() > 0 ) {

                if ( chatItem.getChat().getMentionType().equals("M") ) {

                    footballChatViewHolder.llEmblemContainer.setVisibility(View.VISIBLE);
                    // 매치
                    GlideApp.with(mContext)
                            .load(urls.get(0))
                            .centerCrop()
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.ic_empty_emblem_vector_1)
                            .into(footballChatViewHolder.ivHomeEmblem);

                    GlideApp.with(mContext)
                            .load(urls.get(1))
                            .centerCrop()
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.ic_empty_emblem_vector_1)
                            .into(footballChatViewHolder.ivAwayEmblem);

                } else {

                    footballChatViewHolder.ivMain.setVisibility(View.VISIBLE);

                    // 플레이어, 팀, 컴피티션
                    GlideApp.with(mContext)
                            .load(urls.get(0))
                            .centerCrop()
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.ic_person_grey_vector)
                            .into(footballChatViewHolder.ivMain);
                }

            } else {

                footballChatViewHolder.ivMain.setVisibility(View.VISIBLE);

                // 게시판
                mDrawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(ColorGenerator.MATERIAL.getColor(chatItem.getChat().getBoardName()))
                        .useFont(Typeface.DEFAULT)
                        .fontSize(toPx(38)) /* size in px */
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRect("#", Color.TRANSPARENT);

                footballChatViewHolder.ivMain.setImageDrawable(mDrawable);
            }

            // 플레이어 팀 엠블렘
            if ( chatItem.getChat().getMentionType().equals("P")) {
                footballChatViewHolder.ivEmblem.setVisibility(View.VISIBLE);
                GlideApp.with(mContext)
                        .load(chatItem.getChat().getPlayerEmblem())
                        .centerCrop()
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(footballChatViewHolder.ivEmblem);
            } else {
                footballChatViewHolder.ivEmblem.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clearData() {

        if ( mItems != null ) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    public void onNewData(List<ChatAndAttributeModel> items) {
        final ChatAndAttributeDiffUtil diffCallback = new ChatAndAttributeDiffUtil(mItems, items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mItems.clear();
        this.mItems.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addItems(List<ChatAndAttributeModel> items) {

        int startIndex = mItems.size();
        this.mItems.addAll(items);
        this.notifyItemRangeInserted(startIndex, items.size());
    }

    public ChatAndAttributeModel getItem(int position) {
        return mItems.get(position);
    }

    public int toPx(int dp) {
        Resources resources = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /*-----------------------------------------------------------------------*/

    public class FootballChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_container) LinearLayout llContainer;
        @BindView(R.id.ll_emblem_container) LinearLayout llEmblemContainer;
        @BindView(R.id.iv_home_emblem) ImageView ivHomeEmblem;
        @BindView(R.id.iv_away_emblem) ImageView ivAwayEmblem;
        @BindView(R.id.iv_main) ImageView ivMain;
        @BindView(R.id.tv_board_name) TextView tvBoardName;
        @BindView(R.id.tv_div) TextView tvDiv;
        @BindView(R.id.iv_emblem) ImageView ivEmblem;
        @BindView(R.id.tv_team_name) TextView tvTeamName;
        @BindView(R.id.tv_updated) TextView tvUpdated;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.badge_main) MaterialBadgeTextView badgeMain;

        public FootballChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ChatAndAttributeModel item) {

            if ( item.getChat() != null ) {
                tvBoardName.setText(item.getChat().getBoardName());
                tvTeamName.setText(item.getChat().getPlayerTeamName());
                tvTitle.setText(item.getChat().getTitle());
                tvUpdated.setText(item.getChat().getUpdated());

                if ( item.getChat().getMentionType().equals("P")) {
                    tvDiv.setVisibility(View.VISIBLE);
                } else {
                    tvDiv.setVisibility(View.INVISIBLE);
                }
            }

            if ( item.getAttr() != null ) {

                if ( item.getAttr().isSelected()) {
                    llContainer.setBackgroundColor(mContext.getResources().getColor(R.color.md_light_blue_50));
                } else {
                    llContainer.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_50));
                }

                if ( item.getChat() != null ) {

                    if ( item.getChat().getCount() > item.getAttr().getReadCount()) {
                        badgeMain.setHighLightMode();
                    } else {
                        badgeMain.setBadgeCount(0, true);
                    }
                }
            } else {
                llContainer.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_50));
                badgeMain.setHighLightMode();
            }
        }
    }
}
