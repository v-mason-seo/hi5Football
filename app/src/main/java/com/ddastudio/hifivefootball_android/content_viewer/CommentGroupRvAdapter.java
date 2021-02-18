package com.ddastudio.hifivefootball_android.content_viewer;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.CommentInputModel;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 18..
 */

public class CommentGroupRvAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder>  {

    int targetCommentId;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CommentGroupRvAdapter(List<MultipleItem> data) {
        super(data);

        addItemType(MultipleItem.COMMENT_IN, R.layout.row_comment_in_item);
        addItemType(MultipleItem.COMMENT_OUT, R.layout.row_comment_out_item);
        addItemType(MultipleItem.COMMENT_INPUT, R.layout.row_comment_input_item);
    }

    @Override
    protected int getDefItemViewType(int position) {
        //return super.getDefItemViewType(position);

        MultipleItem item = getItem(position);

        if ( item instanceof CommentModel) {
            return ((CommentModel)item).getDepth() % 2;
        }

        return MultipleItem.COMMENT_INPUT;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {

        if ( helper.getItemViewType() == MultipleItem.COMMENT_IN
                || helper.getItemViewType() == MultipleItem.COMMENT_OUT ) {

            helper.addOnClickListener(R.id.avatar);
            helper.addOnClickListener(R.id.avatar);

            CommentModel commentModel = (CommentModel)item;

            helper.setText(R.id.comment_body, commentModel.isDeleted() ? mContext.getResources().getString(R.string.deleted_comment_info)
                    : commentModel.getContent())
                    .setText(R.id.user_name, commentModel.getUser().getUsername())
                    .setText(R.id.comment_reg_date, commentModel.getCreated())
                    .setText(R.id.comment_like_count, String.valueOf(commentModel.getLikers()))
                    .setImageResource(R.id.comment_like, commentModel.isLiked() ? R.drawable.ic_clap_color_vector2: R.drawable.ic_clap_color_vector)
            ;

            // *** 사용자 프로필 ***
            GlideApp.with(mContext)
                    .load(commentModel.getUser().getAvatarUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.avatar));
        } else if ( helper.getItemViewType() == MultipleItem.COMMENT_INPUT) {

            CommentInputModel commentInput = (CommentInputModel)item;

            helper.addOnClickListener(R.id.avatar)
                    .addOnClickListener(R.id.btn_send)
                    .addOnClickListener(R.id.dismiss_icon)
            .addOnClickListener(R.id.comment_input_text);

            helper.setText(R.id.comment_input_text, commentInput.getContent());

            EditText etInputer = helper.getView(R.id.comment_input_text);
            etInputer.setSelection(etInputer.length());

            UserModel user = App.getAccountManager().getHifiveUser();
            if ( user == null )
                return;

            // *** 사용자 프로필 ***
//            Glide.with(mContext)
//                    .load(user.getAvatarUrl())
//                    .asBitmap()
//                    .centerCrop()
//                    .transform(new CropCircleTransformation(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(CommonUtils.getDrawable(mContext, R.drawable.ic_face))
//                    .into((ImageView)helper.getView(R.id.avatar));
        }
    }

    public int getTargetCommentId() {
        return targetCommentId;
    }

    public void setTargetCommentId(int targetCommentId) {
        this.targetCommentId = targetCommentId;
    }

    public void addCommentInput(int position) {

        CommentModel comment = (CommentModel)getItem(position);

        // 기존에 댓글 에디터가 있는지 체크
        if ( getItemViewType(position) != MultipleItem.COMMENT_INPUT ) {

            if (getItemCount() > position) {

                MultipleItem nextItem = getItem(position + 1);

                if (nextItem instanceof CommentInputModel) {

                    if (((CommentInputModel) nextItem).getParentCommentId() == comment.getCommentId()) {
                        return;
                    }
                }
            }
        }

        // 댓글 에디터 삽입
        CommentInputModel inputModel = new CommentInputModel(comment.getCommentId(), comment.getGroupId(), comment.getDepth()+1, "@" + comment.getUser().getUsername());
        addData(position+1, inputModel);
        notifyDataSetChanged();

        removeCommentInput(comment.getCommentId());
        notifyDataSetChanged();
    }

    public void removeCommentInput(int exceptParentCommentId) {

        Iterator<MultipleItem> iterator = getData().iterator();
        while (iterator.hasNext()) {
            MultipleItem data = iterator.next();
            if ( data instanceof CommentInputModel) {
                try {
                    if ( ((CommentInputModel) data).getParentCommentId() != exceptParentCommentId)
                    iterator.remove();
                } catch (Exception ex) {
                }
            }
        }
    }

    public void removeAllCommentInput() {

        Iterator<MultipleItem> iterator = getData().iterator();
        while (iterator.hasNext()) {
            MultipleItem data = iterator.next();
            if ( data instanceof CommentInputModel) {
                try {
                    iterator.remove();
                } catch (Exception ex) {
                }
            }
        }
    }


    public void addCommentData(List<CommentModel> items) {

        // 1. 가장 높은 depth를 가진 댓글을 가져온다.
        CommentModel maxComment = Collections.max(items, new Comparator<CommentModel>() {
            @Override
            public int compare(CommentModel commentModel, CommentModel t1) {
                return Integer.compare(commentModel.getDepth(), t1.getDepth());
            }
        });

        // 2. depth별로 정렬한다.
        Collections.sort(items, new Comparator<CommentModel>() {
            @Override
            public int compare(CommentModel commentModel, CommentModel t1) {
                int v = Integer.compare(commentModel.getDepth(), t1.getDepth());

                if ( v != 0 )
                    return v;

                return Integer.compare(commentModel.getCommentId(), t1.getCommentId());
            }
        });

        // 3. 기존 댓글 정보를 삭제한다.
        removeAllCommentInput();

        List<CommentModel> sortedList = setChild(1, null, items);

        bindData(sortedList);

        // TODO: 2017. 9. 23.  아래함수를 호출하지 않으면 오류가 발생한다 ( 문제있음 )
        notifyDataSetChanged();

//        List<MultipleItem> list = getData();
//
//        int position = -1;
//        for ( int i = 0 ; i < list.size(); i++ ) {
//
//            if ( list.get(i) instanceof CommentModel ) {
//
//                if ( ((CommentModel) list.get(i)).getCommentId() == targetCommentId ) {
//                    position = i;
//                    break;
//                }
//            }
//        }
//
//        addCommentInput(position);

    }

    /**
     * 대상 댓글 위치를 찾는다 ( 화면이동 )
     * @return
     */
    public int findTargetPosition() {

        List<MultipleItem> list = getData();

        for ( int i = 0 ; i < list.size(); i++ ) {

            if ( list.get(i) instanceof CommentModel ) {

                if ( ((CommentModel) list.get(i)).getCommentId() == targetCommentId ) {
                    return i;
                }
            }
        }

        return -1;
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

    private List<CommentModel> setChild(int depth, CommentModel comment, List<CommentModel> comments) {

        List<CommentModel> root = null;
        List<CommentModel> filterComments = getChildComments(comments, depth, comment == null ? -1 : comment.getCommentId());

//        List<CommentModel> filterComments = Lists.newArrayList(Collections2.filter(comments, new Predicate<CommentModel>() {
//            @Override
//            public boolean apply(@javax.annotation.Nullable CommentModel input) {
//                return (depth == 1 && input.getDepth() == depth)
//                        || ( input.getDepth() == depth && comment.getCommentId() == input.getParentId());
//            }
//        }));

        if ( depth == 1) {
            root = filterComments;
        } else {
            comment.addChild(filterComments);
        }

        for ( int i = 0; i < filterComments.size(); i++ )
        {
            setChild(depth + 1, filterComments.get(i), comments);
        }

        return root;
    }

    private void bindData(List<CommentModel> items) {

        if ( items == null ) return;

        for ( int i = 0; i < items.size(); i++) {

            Log.d("tree", "[last] "+items.get(i).getContent());
            addData(items.get(i));
            bindData(items.get(i).getChildeNode());
        }
    }
}
