package com.ddastudio.hifivefootball_android.content_report;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.CommonInfoModel;
import com.ddastudio.hifivefootball_android.football_chat.FootballChatRvAdapter;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentReportRvAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<CommonInfoModel> mItems;


    /**
     * 생성자1
     * @param context
     */
    public ContentReportRvAdpater(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }


    /**
     * 생성자2
     * @param context
     * @param items
     */
    public ContentReportRvAdpater(Context context, List<CommonInfoModel> items) {
        mContext = context;
        mItems = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ContentReportViewHolder(inflater.inflate(R.layout.row_content_report_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CommonInfoModel item = mItems.get(position);
        ContentReportViewHolder viewHolder = (ContentReportViewHolder)holder;

        viewHolder.bindData(item);

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(ColorGenerator.MATERIAL.getColor(position+1))
                .useFont(Typeface.DEFAULT)
                .fontSize(toPx(38)) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRect(""+(position+1), Color.TRANSPARENT);

        viewHolder.ivSeq.setImageDrawable(textDrawable);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public CommonInfoModel getItem(int position) {

        return mItems.get(position);
    }

    public int toPx(int dp) {
        Resources resources = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /*-----------------------------------------------------------------------*/

    public class ContentReportViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_seq) ImageView ivSeq;
        @BindView(R.id.tv_title) TextView tvTitle;

        public ContentReportViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(CommonInfoModel item) {

            tvTitle.setText(item.getCodeName());
        }
    }
}
