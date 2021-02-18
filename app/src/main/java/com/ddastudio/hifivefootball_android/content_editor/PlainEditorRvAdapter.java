package com.ddastudio.hifivefootball_android.content_editor;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorImageModel;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorLinkModel;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorTextModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.community.EditorTitleModel;
import com.ddastudio.hifivefootball_android.data.model.community.EditorVideoModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class PlainEditorRvAdapter extends BaseItemDraggableAdapter<MultiItemEntity, BaseViewHolder>
{

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PlainEditorRvAdapter(List<MultiItemEntity> data) {
        super(data);

        //addItemType(MultipleItem.EDITOR_TITLE, R.layout.row_editor_title_item);
        addItemType(MultipleItem.EDITOR_TEXT, R.layout.row_editor_text_item);
        addItemType(MultipleItem.EDITOR_IMAGE, R.layout.row_editor_image_item);
        addItemType(MultipleItem.EDITOR_LINK, R.layout.row_editor_link_item);
        addItemType(MultipleItem.EDITOR_VIDEO, R.layout.row_editor_video_item);
    }

    private SparseIntArray layouts;

    private static final int DEFAULT_VIEW_TYPE = -0xff;
    public static final int TYPE_NOT_FOUND = -404;

    @Override
    protected int getDefItemViewType(int position) {
        Object item = mData.get(position);
        if (item instanceof MultiItemEntity) {
            return ((MultiItemEntity) item).getItemType();
        }
        return DEFAULT_VIEW_TYPE;
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder bh = createBaseViewHolder(parent, getLayoutId(viewType));

        return bh;
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType, TYPE_NOT_FOUND);
    }

    protected void addItemType(int type, @LayoutRes int layoutResId) {
        if (layouts == null) {
            layouts = new SparseIntArray();
        }
        layouts.put(type, layoutResId);
    }


    /*-----------------------------------------------------------------------*/

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        //bindText(helper, item);
        switch (helper.getItemViewType()) {
//            case MultipleItem.EDITOR_TITLE:
//                bindTitle(helper, (EditorTitleModel)item);
//                break;
            case MultipleItem.EDITOR_TEXT:
                bindText(helper, item);
                break;
            case MultipleItem.EDITOR_IMAGE:
                bindImage(helper, item);
                break;
            case MultipleItem.EDITOR_LINK:
                bindLink(helper, item);
                break;
            case MultipleItem.EDITOR_VIDEO:
                bindVideo(helper, (EditorVideoModel)item);
                break;
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    private void bindTitle(BaseViewHolder helper, EditorTitleModel data) {

        helper.setText(R.id.tv_title, data.getTitle());
    }

    private void bindVideo(BaseViewHolder helper, EditorVideoModel data) {

        TextView text = helper.getView(R.id.editor_text);
        text.setText(data.getUrl());

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                    .withBorder(4) /* thickness in px */
                    .fontSize(toPx(18))
                .endConfig()
                .buildRoundRect("MP4", ColorGenerator.MATERIAL.getColor("MP4"), 10);

        ((ImageView)helper.getView(R.id.iv_mp4_icon)).setImageDrawable(drawable);
    }

    public int toPx(int dp) {
        Resources resources = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    private void bindText(BaseViewHolder helper, MultiItemEntity data) {

        EditorTextModel item = (EditorTextModel)data;
        TextView text = helper.getView(R.id.editor_text);
        text.setText(item.getMainText());
        // TODO: 2017. 9. 28. 포커스를 주고 키보드가 올라와야 한다.
        //etText.requestFocus();
    }

    private void bindImage(BaseViewHolder helper, MultiItemEntity data) {

        EditorImageModel item = (EditorImageModel)data;
        helper.addOnClickListener(R.id.editor_selected);
        RadioButton radioButton = helper.getView(R.id.editor_selected);

        helper.setText(R.id.editor_image_caption, item.getUrl())
        .setVisible(R.id.editor_image_representation, item.isSelected())
        //.setChecked(R.id.editor_selected, item.isSelected())
        ;

        //radioButton.setSelected(true);
        radioButton.setChecked(item.isSelected());

        GlideApp.with(mContext)
                .load(item.getUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.editor_image));
    }


    private void bindLink(BaseViewHolder helper, MultiItemEntity data) {

        EditorLinkModel item = (EditorLinkModel)data;

        helper.setText(R.id.editor_link, item.getLink())
        .setGone(R.id.ogmt_container, item.getOg() != null);

        if ( item.getOg() != null ) {

            helper.setText(R.id.editor_link_title, item.getOg().getTitle())
                    .setText(R.id.editor_link_description, item.getOg().getDescription());

            GlideApp.with(mContext)
                    .load(item.getOg().getImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.editor_link_image));
        }
    }

    /*---------------------------------------------------------*/

    public void setAllSelectable(boolean isSelectable) {

        for ( int i = 0; i < getData().size(); i++) {

            MultiItemEntity item = getData().get(i);

            if ( item instanceof EditorImageModel ) {

                EditorImageModel imageModel = (EditorImageModel)item;
                imageModel.setSelected(isSelectable);
            }
        }
    }

}
