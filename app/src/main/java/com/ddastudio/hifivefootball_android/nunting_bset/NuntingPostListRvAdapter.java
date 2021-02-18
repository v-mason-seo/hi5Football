package com.ddastudio.hifivefootball_android.nunting_bset;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsHeaderModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsListModel;

import java.util.List;

public class NuntingPostListRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public NuntingPostListRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(NuntingViewType.POSTS_HEADER, R.layout.row_nunting_post_list_header_row);
        addItemType(NuntingViewType.POSTS, R.layout.row_nunting_post_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case NuntingViewType.POSTS_HEADER:
                bindPostsHeader(helper, (PostsHeaderModel)item);
                break;
            case NuntingViewType.POSTS:
                bindPosts(helper, (PostsListModel)item);
                break;
        }
    }

    private void bindPostsHeader(BaseViewHolder helper, PostsHeaderModel header) {
        helper.setText(R.id.tv_header, header.getHeaderFormat());
    }

    private void bindPosts(BaseViewHolder helper, PostsListModel posts) {

        helper.setText(R.id.tvTitle, posts.getTitle());
    }
}
