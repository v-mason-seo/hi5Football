package com.ddastudio.hifivefootball_android.content_viewer;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.content.ContentRelationModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.github.zagum.switchicon.SwitchIconView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 14..
 */

public class RelationContentRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public RelationContentRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.SIMPLE_SECTION_HEADER, R.layout.row_simple_section_item);
        addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_list_item2);
        addItemType(MultipleItem.ARENA_SCHEDULE, R.layout.row_simple_match_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {

            case ViewType.SIMPLE_SECTION_HEADER:
                bindSimpleSection(helper, (SimpleSectionHeaderModel)item);
                break;

            case ViewType.CONTENT_GENERAL:
                ContentHeaderModel relation = (ContentHeaderModel)item;
                bindRelationData2(helper, relation);
                break;

            case MultipleItem.ARENA_SCHEDULE:
                bindMatch(helper, (MatchModel)item);
                break;
        }
    }

    /*-----------------------------------------------------------------------------*/

    private void bindSimpleSection(BaseViewHolder helper, SimpleSectionHeaderModel header) {

        helper.setText(R.id.tv_header, header.getTitle());
    }

    /**
     * 연관 게시글
     * @param helper
     * @param content
     */
    private void bindRelationData2(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count);

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.board_name, PostBoardType.toEnum(content.getBoardId()).toString())
                .setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
        ;

        /**
         * 게시판 - 작성자 - 작성시간 - 하이파이브 - 스크랩
         */
        int start;
        int end;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        start = ssb.length();
        ssb.append(content.getNickName());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_green_400)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        start = ssb.length();
        ssb.append("  ").append(content.getUpdatedString());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_grey_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if ( content.getLikers() > 0) {
            start = ssb.length();
            ssb.append("  ").append(String.valueOf(content.getLikers())).append(" 하이파이브");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ( content.isScraped() ) {
            start = ssb.length();
            ssb.append("  " + "스크랩됨");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        helper.setText(R.id.tv_detail_info, ssb);

        // *** 사용자 프로필 ***
        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_face)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_face))
                .into((ImageView)helper.getView(R.id.avatar));
    }

    private void bindRelationData(BaseViewHolder helper, ContentRelationModel relation) {

        helper.addOnClickListener(R.id.relation_container);

        helper.setText(R.id.relation_title, getTitleNBoardTag(relation.getBoardName(), relation.getTitle()))
                .setText(R.id.user_name, relation.getUser().getUsername())
                .setText(R.id.reg_date, relation.getCreatedString())
        ;

        // *** 사용자 프로필 ***
        GlideApp.with(mContext)
                .load(relation.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_person)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_person))
                .into((ImageView)helper.getView(R.id.relation_avatar));
    }

    private int getScoreColor(String src_score, String target_score) {

        Integer src;
        Integer target;

        try {
            src = Integer.parseInt(src_score);
        } catch (NumberFormatException e) {
            src = 0;
        }

        try {
            target = Integer.parseInt(target_score);
        } catch (NumberFormatException e) {
            target = 0;
        }

        if ( src > target ) {
            return ContextCompat.getColor(mContext, R.color.md_blue_700);
        } else {
            return ContextCompat.getColor(mContext, R.color.md_grey_700);
        }
    }

    private void bindMatch(BaseViewHolder helper, MatchModel match) {

        helper.addOnClickListener(R.id.hometeam_image)
                .addOnClickListener(R.id.awayteam_image);

        helper.setText(R.id.hometeam_name, match.getHomeTeamName())
                .setText(R.id.awayteam_name, match.getAwayTeamName())
                .setText(R.id.match_time, match.getTime())
                .setGone(R.id.match_time, !match.getStatus().equals("FT"))
                .setGone(R.id.ll_score, match.getStatus().equals("FT"))
                .setText(R.id.tv_match_id, match.getMatchId() + " / " + match.getMatchFaId())
                .setText(R.id.home_team_score, match.getHomeTeamScore())
                .setText(R.id.away_team_score, match.getAwayTeamScore())
                .setTextColor(R.id.home_team_score, getScoreColor(match.getHomeTeamScore(), match.getAwayTeamScore()))
                .setTextColor(R.id.away_team_score, getScoreColor(match.getAwayTeamScore(), match.getHomeTeamScore()))
        ;

        // --- Local team ---
        GlideApp.with(mContext)
                .load(match.getHomeTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView)helper.getView(R.id.hometeam_image));

        // -- Visitor team ---
        GlideApp.with(mContext)
                .load(match.getAwayTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView)helper.getView(R.id.awayteam_image));
    }

//    private void bindCommentInfoData(BaseViewHolder helper, ContentCommentInfoModel commentInfoData) {
//
//        helper.setText(R.id.comment_count_info, commentInfoData.getCommentCountString());
//    }
//
//    private void bindHifiveData(BaseViewHolder helper, ContentClapsModel hifiveData) {
//
//        helper.addOnClickListener(R.id.undo_hifive)
//                .addOnClickListener(R.id.content_report);
//
//        helper.setText(R.id.hifive_count, hifiveData.getLikeCountString())
//            .setText(R.id.tv_scrap_count, hifiveData.getScrapCountString());
//    }
//
//    private void bindTagList(BaseViewHolder helper, ContentTagListModel tags) {
//
//        //helper.setGone(R.id.fbx_tag, false);
//
//        if ( tags.getTags() != null && tags.getTags().size() > 0) {
//            ChipCloudConfig config = new ChipCloudConfig()
//                    .selectMode(ChipCloud.SelectMode.none)
//                    .checkedChipColor(Color.parseColor("#ddaa00"))
//                    .checkedTextColor(Color.parseColor("#ffffff"))
//                    //.uncheckedChipColor(Color.parseColor("#e0e0e0"))
//                    .uncheckedChipColor(mContext.getResources().getColor(R.color.md_indigo_50))
//                    .uncheckedTextColor(mContext.getResources().getColor(R.color.md_grey_800))
//                    // 아래코드는 삭제 아이콘 표시 여부
//                /*.showClose(Color.parseColor("#a6a6a6"), 500)*/;
//
//            FlexboxLayout fbl = helper.getView(R.id.fbx_tag);
//            fbl.removeAllViews();
//            ChipCloud chipCloud = new ChipCloud(mContext, fbl, config);
//
//            for ( int i = 0; i < tags.getTags().size(); i++ ) {
//                chipCloud.addChip(tags.getTags().get(i));
//            }
//        } else {
//            //helper.setGone(R.id.fbx_tag, false);
//        }
//
//    }
//
//    private void bindWriterData(BaseViewHolder helper, ContentWriterModel writer) {
//
//        helper.addOnClickListener(R.id.iv_avatar);
//
//        helper.setText(R.id.user_name, writer.getNickNameNUserName())
//                .setText(R.id.tv_user_profile, writer.getProfile())
//                .setText(R.id.reg_date, writer.getCreated());
//
//        // *** 사용자 프로필 ***
//        if ( writer.getUser() != null ) {
//            Glide.with(mContext)
//                    .load(writer.getUser().getAvatarUrl())
//                    .asBitmap()
//                    .centerCrop()
//                    .transform(new CropCircleTransformation(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.ic_face)
//                    .into((ImageView)helper.getView(R.id.iv_avatar));
//        }
//    }

    /**
     * 바디 데이터 바인딩
     * @param helper
     * @param bodyModel
     */
    /*private void bindBodyData(BaseViewHolder helper, ContentModel bodyModel) {

        helper.addOnLongClickListener(R.id.post_content_view_body);

//        helper.setText(R.id.comment_count_info, "댓글 " + String.valueOf(bodyModel.getComments()))
//              .setVisible(R.id.fbx_tag, !(bodyModel.getTags() != null && bodyModel.getTags().size() > 0));

        ObservableWebView wvBody = helper.getView(R.id.post_content_view_body);
        String html = bodyModel.getWrapperHtml(mContext);
        if (!TextUtils.isEmpty(html)) {
            wvBody.loadDataWithBaseURL(null, html, "text/html", "utf-8", null );
        }

        // *** Tag ***
        if ( bodyModel.getTags() != null && bodyModel.getTags().size() > 0 ) {

            *//*ChipCloudConfig config = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.none)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    //.uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .uncheckedChipColor(mContext.getResources().getColor(R.color.md_grey_100))
                    .uncheckedTextColor(Color.parseColor("#000000"))
                    // 아래코드는 삭제 아이콘 표시 여부
                    *//**//*.showClose(Color.parseColor("#a6a6a6"), 500)*//**//*;

            FlexboxLayout fbl = helper.getView(R.id.fbx_tag);
            fbl.removeAllViews();
            ChipCloud chipCloud = new ChipCloud(mContext, fbl, config);

            for ( int i = 0; i < bodyModel.getTags().size(); i++ ) {
                chipCloud.addChip(bodyModel.getTags().get(i));
            }*//*
        }
        *//*if ( !TextUtils.isEmpty(bodyModel.getTags())) {

            String[] tags = bodyModel.getTags().split(",");

            if ( tags != null && tags.length > 0 ) {
                ChipCloudConfig config = new ChipCloudConfig()
                        .selectMode(ChipCloud.SelectMode.none)
                        .checkedChipColor(Color.parseColor("#ddaa00"))
                        .checkedTextColor(Color.parseColor("#ffffff"))
                        //.uncheckedChipColor(Color.parseColor("#e0e0e0"))
                        .uncheckedChipColor(mContext.getResources().getColor(R.color.md_grey_100))
                        .uncheckedTextColor(Color.parseColor("#000000"))
                        // 아래코드는 삭제 아이콘 표시 여부
                *//**//*.showClose(Color.parseColor("#a6a6a6"), 500)*//**//*;

                FlexboxLayout fbl = helper.getView(R.id.fbx_tag);
                fbl.removeAllViews();
                ChipCloud chipCloud = new ChipCloud(mContext, fbl, config);

                for ( int i =0; i < tags.length; i++) {
                    chipCloud.addChip(tags[i]);
                }
            }
        }*//*
    }*/

    /**
     * 댓글 데이터 바인딩
     * @param helper
     * @param commentModel
     */
    private void bindCommentData(BaseViewHolder helper, CommentModel commentModel) {

        LinearLayout container = helper.getView(R.id.comment_container);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMarginStart(25 * (commentModel.getDepth()));
        container.setLayoutParams(params);

        // *** 클릭 리스너 등록 ***
        helper.addOnClickListener(R.id.comment_like)
              .addOnClickListener(R.id.comment_like_count)
              .addOnClickListener(R.id.avatar)
              .addOnClickListener(R.id.btn_reply_comment)
              .addOnClickListener(R.id.btn_delete_comment)
              .addOnClickListener(R.id.btn_edit_comment)
        ;

        // *** 데이터 바인딩 ***
        helper.setText(R.id.comment_title, commentModel.isDeleted() ? mContext.getResources().getString(R.string.deleted_comment_info)
                : commentModel.getContent())
                .setText(R.id.comment_user_name, commentModel.getUser().getUsername() + " - " + commentModel.getCommentId())
                .setText(R.id.comment_reg_date, commentModel.getCreated())
                .setText(R.id.comment_like_count, String.valueOf(commentModel.getLikers()))
                .setGone(R.id.btn_reply_comment, !commentModel.isDeleted())
                .setGone(R.id.post_recomment_arrow, commentModel.getDepth() > 1)
                //.setGone(R.id.comment_depth, commentModel.getDepth() > 1)
                //.setText(R.id.comment_depth, String.valueOf(commentModel.getDepth()))
                .setGone(R.id.btn_delete_comment, App.getAccountManager().isSameUser(commentModel.getUser().getUsername()))
                .setGone(R.id.btn_edit_comment, App.getAccountManager().isSameUser(commentModel.getUser().getUsername()))
                .setTextColor(R.id.comment_user_name, App.getAccountManager().isSameUser(commentModel.getUser().getUsername())
                        ?mContext.getResources().getColor(R.color.md_blue_400)
                        :mContext.getResources().getColor(R.color.md_indigo_400))
                //.setImageResource(R.id.comment_like, commentModel.isLiked() ? R.drawable.ic_like_color : R.drawable.ic_like)
                ;


        ((SwitchIconView)helper.getView(R.id.comment_like)).setIconEnabled(commentModel.isLiked());


        // *** 사용자 프로필 ***
        GlideApp.with(mContext)
                .load(commentModel.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_face)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_face))
                .into((ImageView)helper.getView(R.id.avatar));
    }





    /*--------------------------------------------------------------*/

    /**
     * 게시판 태그와 게시글 제목을 가져온다.
     * @param boardName
     * @param title
     * @return
     */
    private SpannableString getTitleNBoardTag(String boardName, String title) {

        final BadgeDrawable drawableBoardTag =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(ColorGenerator.MATERIAL.getColor(boardName))
                        .textSize(CommonUtils.sp2px(mContext, 12))
                        .text1(boardName)
                        .build();

        SpannableString spannableString = new SpannableString(TextUtils.concat(drawableBoardTag.toSpannable(), "  ", title));

        return spannableString;
    }

    /*--------------------------------------------------------------*/

    /**
     * 댓글 데이터만 삭제한다.
     */
    public void removeCommentData() {

        Iterator<MultiItemEntity> iterator = getData().iterator();
        while (iterator.hasNext()) {
            MultiItemEntity data = iterator.next();
            if ( data instanceof CommentModel)
                iterator.remove();
        }
    }

    public void removeRelationData() {

        Iterator<MultiItemEntity> iterator = getData().iterator();
        while (iterator.hasNext()) {
            MultiItemEntity data = iterator.next();
            if ( data instanceof ContentRelationModel)
                iterator.remove();
        }
    }

    public void addRelationData(List<ContentRelationModel> items) {

        removeRelationData();
    }

    public void addCommentData(List<CommentModel> items) {

        // 1. 가장 높은 depth를 가진 댓글을 가져온다.
//        CommentModel maxComment = Collections.max(items, new Comparator<CommentModel>() {
//            @Override
//            public int compare(CommentModel commentModel, CommentModel t1) {
//                return Integer.compare(commentModel.getDepth(), t1.getDepth());
//            }
//        });

        // 2. depth별로 정렬한다.
//        Collections.sort(items, new Comparator<CommentModel>() {
//            @Override
//            public int compare(CommentModel commentModel, CommentModel t1) {
//                int v = Integer.compare(commentModel.getDepth(), t1.getDepth());
//
//                if ( v != 0 )
//                    return v;
//
//                return Integer.compare(commentModel.getCommentId(), t1.getCommentId());
//            }
//        });

        // 3. 기존 댓글 정보를 삭제한다.
        removeCommentData();

        List<CommentModel> sortedList = setChild(1, null, items);

        bindData(sortedList);

        // TODO: 2017. 9. 23.  아래함수를 호출하지 않으면 오류가 발생한다 ( 문제있음 )
        notifyDataSetChanged();
    }

    private List<CommentModel> setChild(int depth, CommentModel comment, List<CommentModel> comments) {

        List<CommentModel> root = null;
        List<CommentModel> filterList = getChildComments(comments, depth, comment == null ? -1 : comment.getCommentId());

        if ( depth == 1) {
            root = filterList;
        } else {
            comment.addChild(filterList);
        }

        for ( int i = 0; i < filterList.size(); i++ )
        {
            setChild(depth + 1, filterList.get(i), comments);
        }

        return root;
    }

    /**
     * commentId를 부모로 가진 하우 대댓글 리스트를 가져온다
     * @param comments
     * @param depth
     * @param commentId
     * @return
     */
    private List<CommentModel> getChildComments(List<CommentModel> comments, int depth, int commentId) {

        List<CommentModel> filterList = new ArrayList<>();
        for ( int i=0; i < comments.size(); i++) {

            CommentModel data = comments.get(i);

            if (  ( depth == 1 && data.getDepth() == depth)
                    || ( data.getDepth() == depth && commentId == data.getParentId()) ) {
                filterList.add(data);
            }
        }

        return filterList;
    }

    private void bindData(List<CommentModel> items) {

        if ( items == null )
            return;

        for ( int i = 0; i < items.size(); i++) {

            addData(items.get(i));
            bindData(items.get(i).getChildeNode());
        }
    }


}
