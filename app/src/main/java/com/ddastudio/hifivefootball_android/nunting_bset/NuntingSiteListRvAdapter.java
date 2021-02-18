package com.ddastudio.hifivefootball_android.nunting_bset;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NuntingSiteListRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public NuntingSiteListRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(NuntingViewType.SITE_LIST, R.layout.row_site_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case NuntingViewType.SITE_LIST:
                bindSite(helper, (SiteBoardModel)item);
                break;
        }
    }

    private void bindSite(BaseViewHolder helper, SiteBoardModel site) {

        StringBuilder boardNames = new StringBuilder();
        if ( site.getBoards() != null && site.getBoards().size() > 0 ) {

            for (int i = 0; i < site.getBoards().size(); i ++ ) {

                if ( i > 0 ) {
                    boardNames.append(", ").append(site.getBoards().get(i).getBnm());
                } else {
                    boardNames = new StringBuilder(site.getBoards().get(i).getBnm());
                }
            }
        }

        helper.setText(R.id.tv_site_name, site.getName().getNm())
                .setText(R.id.tv_board_name, boardNames.toString());

        GlideApp.with(mContext)
                .load(site.getSiteLogo())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_circle_grey_vector)
                .into((ImageView)helper.getView(R.id.iv_site_logo));
    }
}
