package com.ddastudio.hifivefootball_android.content_list;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.picasso.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FootballRelatedContentListRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public FootballRelatedContentListRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_default_card_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            // 일반게시글
            case ViewType.CONTENT_GENERAL:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                //bindGeneralContent(helper, (ContentHeaderModel)item);
                break;
        }
    }

    /*------------------------------------------------------------------------*/

    private void bindDefaultCardContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.iv_avatar)
                .addOnClickListener(R.id.include_team);

        helper.setText(R.id.tv_nickname, content.getNickNameAndUserName())
                .setText(R.id.tv_updated, content.getUpdatedString())
                .setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_hifive_count, content.getLikerString())
                .setText(R.id.tv_comment_count, content.getCommentString())
                .setText(R.id.tv_preview, content.getPreview())
                .setGone(R.id.tv_preview, !TextUtils.isEmpty(content.getPreview()))
                .setGone(R.id.include_match, content.getArenaId() != 0)
                .setGone(R.id.include_team, content.getTeam() != null)
                .setGone(R.id.include_player, content.getPlayer() != null)
                .setGone(R.id.v_divider, !(content.getArenaId() !=0 || content.getTeam() != null || content.getPlayer() != null))
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
        ;

        displayImageByPicasso(content.getUser().getAvatarUrl(),
                helper.getView(R.id.iv_avatar),
                R.drawable.ic_face);

        if ( content.getImgs() != null && content.getImgs().size() > 0) {

            GlideApp.with(mContext)
                    .load(content.getImgs().get(0).getStreamUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_dracula)
                    .into((ImageView)helper.getView(R.id.iv_image));
        } else {
            helper.setGone(R.id.iv_image, false);
        }

        //
        // 2. 매치정보
        //
        if ( content.getMatch() != null ) {

            helper.setText(R.id.tv_competition_name, content.getCompetitionName() + " - " + content.getWeek())
                    .setText(R.id.tv_match_date, content.getMatch().getMatchDate4())
                    .setText(R.id.tv_home_team_name, content.getMatch().getHomeTeamName())
                    .setText(R.id.tv_home_team_score, content.getMatch().getHomeTeamScoreString())
                    .setText(R.id.tv_away_team_name, content.getMatch().getAwayTeamName())
                    .setText(R.id.tv_away_team_score, content.getMatch().getAwayTeamScoreString());

            // 컴피티션
            displayImageByPicasso(content.getCompetitionImage(),
                    helper.getView(R.id.iv_competition),
                    R.drawable.ic_circle_grey_vector);

            // 홈팀 엠블럼
            Picasso.get()
                    .load(content.getHomeTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_home_emblem));

            // 어웨이팀 엠블럼
            Picasso.get()
                    .load(content.getAwayTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_away_emblem));
        }

        //
        // 2. 선수 정보
        //
        if ( content.getPlayer() != null ) {
            helper.setText(R.id.tv_player_name, content.getPlayerName())
                    .setText(R.id.tv_player_team_name, content.getPlayer().getTeamName());

            displayImageByPicasso(content.getPlayerAvatar(),
                    helper.getView(R.id.iv_player_avatar),
                    R.drawable.ic_person_grey_vector);
        }

        //
        // 2. 팀 정보
        //
        if ( content.getTeam() != null ) {
            helper.setText(R.id.tv_team_name, content.getTeam().getTeamName());

            displayImageByPicasso(content.getTeamEmblemUrl(),
                    helper.getView(R.id.iv_emblem),
                    R.drawable.ic_empty_emblem_vector_1);
        }

    }

    /*------------------------------------------------------------------------*/

    private boolean displayImageByPicasso(String url, ImageView target, int placeholder_id) {

        if ( TextUtils.isEmpty(url)) {
            target.setImageResource(R.drawable.ic_face);
            return false;
        }

        Picasso.get()
                .load(url)
                .placeholder(placeholder_id)
                .transform(new PicassoCircleTransformation())
                .fit()
                .centerInside()
                .into(target);

        return true;
    }
}
