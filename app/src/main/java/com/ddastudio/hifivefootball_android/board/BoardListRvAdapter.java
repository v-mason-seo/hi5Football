package com.ddastudio.hifivefootball_android.board;

import android.support.v7.util.DiffUtil;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;

import java.util.List;

public class BoardListRvAdapter extends BaseMultiItemQuickAdapter<BoardMasterModel, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BoardListRvAdapter(List<BoardMasterModel> data) {
        super(data);

        addItemType(ViewType.BOARD, R.layout.row_board_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, BoardMasterModel item) {
        switch (helper.getItemViewType()) {
            // 일반게시글
            case ViewType.BOARD:
                bindBoard(helper, item);
                //bindGeneralContent(helper, (ContentHeaderModel)item);
                break;
        }
    }

    public void onNewData(List<BoardMasterModel> items) {
        final BoardListDiffCallback diffCallback = new BoardListDiffCallback(getData(), items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.getData().clear();
        this.getData().addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    /*------------------------------------------------------------------------*/

    private void bindBoard(BaseViewHolder helper, BoardMasterModel board) {

        helper.addOnClickListener(R.id.btn_best_content);

        helper.setText(R.id.tv_board, board.getSubtitle())
            .setGone(R.id.btn_best_content, board.getHasBest() == 1)
            .setBackgroundRes(R.id.ll_board_container, board.isSelected() == 1 ? R.color.md_grey_200 : R.color.md_grey_50)
            ;
    }
}
