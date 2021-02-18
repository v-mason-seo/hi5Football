package com.ddastudio.hifivefootball_android.content_viewer;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 3. 4..
 */

public class CommentRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CommentRvAdapter(List<MultiItemEntity> data) {
        super(data);

        //addItemType(MultipleItem.CONTENT_COMMENT_INFO, R.layout.row_content_viewer_comment_info_item);
        addItemType(MultipleItem.CONTENT_COMMENT_LIST, R.layout.row_comment_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case MultipleItem.CONTENT_COMMENT_LIST:
                CommentModel commentModel = (CommentModel)item;
                bindCommentData(helper, commentModel);
                break;
        }
    }

    /**
     * Adapter.setHasStableIds(boolean) 설정을 해줘야 한다.
     * 같은 id를 갖는 데이터는 따로 바인딩 작업을 하지 않아 선능을 향상시킬 수 있다.
     * 같은 데이터를 중복으로 보여줄 경우 유용하다.
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    /*----------------------------------------------------------------------------------------------*/

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
                .addOnClickListener(R.id.btn_report_comment)
        ;

        // *** 데이터 바인딩 ***
        helper.setText(R.id.comment_title, commentModel.getContent())
                .setTextColor(R.id.comment_title, getCommentBodyTextColor(commentModel.isDeleted()))
                .setText(R.id.comment_user_name, commentModel.getUser().getNickname())
                .setText(R.id.comment_reg_date, commentModel.getCreated())
                .setText(R.id.comment_like_count, String.valueOf(commentModel.getLikers()))
                .setGone(R.id.ll_comment_user_action, !commentModel.isDeleted())
                .setGone(R.id.post_recomment_arrow, commentModel.getDepth() > 1)
                .setGone(R.id.btn_delete_comment, App.getAccountManager().isSameUser(commentModel.getUser().getUsername()))
                .setGone(R.id.btn_edit_comment, App.getAccountManager().isSameUser(commentModel.getUser().getUsername()))
                .setGone(R.id.tv_is_edit_comment, commentModel.isEdit() && !commentModel.isDeleted())
                .setGone(R.id.tv_is_report_comment, commentModel.getReported() != 0)
                .setBackgroundColor(R.id.fl_comment, getCommentBackground(commentModel.getDepth()))
                .setTextColor(R.id.comment_user_name, App.getAccountManager().isSameUser(commentModel.getUser().getUsername())
                        ? mContext.getResources().getColor(R.color.md_blue_400)
                        : mContext.getResources().getColor(R.color.md_indigo_400))
        ;

        if ( commentModel.isDeleted()) {
            TextView tv = (TextView)helper.getView(R.id.comment_title);
            tv.setTypeface(tv.getTypeface(), Typeface.ITALIC);
        }

        // *** 사용자 프로필 ***
        GlideApp.with(mContext)
                .load(commentModel.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }

    /*----------------------------------------------------------------------------------------------*/

    public void removeCommentData() {

        Iterator<MultiItemEntity> iterator = getData().iterator();
        while (iterator.hasNext()) {
            MultiItemEntity data = iterator.next();
            if ( data instanceof CommentModel)
                iterator.remove();
        }
    }

    public void addCommentData(List<CommentModel> items) {

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

    /*----------------------------------------------------------------------------------------------*/

    private int getCommentBodyTextColor(boolean isDeleted) {

        if ( isDeleted) {
            return ContextCompat.getColor(mContext, R.color.md_grey_500);
        }

        return ContextCompat.getColor(mContext, R.color.md_grey_800);

    }

    private int getCommentBackground(int depth) {

        if ( depth == 2) {
            return ContextCompat.getColor(mContext, R.color.md_grey_100);
        } else if ( depth == 3 ) {
            return ContextCompat.getColor(mContext, R.color.md_grey_200);
        } else if ( depth == 4 ) {
            return ContextCompat.getColor(mContext, R.color.md_grey_300);
        } else if ( depth == 5 ) {
            return ContextCompat.getColor(mContext, R.color.md_grey_400);
        } else if ( depth == 6 ) {
            return ContextCompat.getColor(mContext, R.color.md_grey_500);
        }

        return ContextCompat.getColor(mContext, R.color.md_grey_50);
    }
}
