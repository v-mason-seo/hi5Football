package com.ddastudio.hifivefootball_android.match_summery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.match_summery.model.CompetitionSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.MatchSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionModel;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.SummeryBaseWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.TeamSectionWrapperModel;
import com.ddastudio.hifivefootball_android.ui.utils.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;

public class SummeryContainerRvAdapter extends RecyclerView.Adapter<SummeryContainerRvAdapter.ViewHolder> {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private Context mContext;
    private List<SummeryBaseWrapperModel> mList;

    public SummeryContainerRvAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    public void addItem(SummeryBaseWrapperModel item) {
        this.mList.add(item);
        notifyDataSetChanged();
    }

    public void addItems(List<? extends SummeryBaseWrapperModel> items) {
        this.mList.addAll(items);
        notifyDataSetChanged();
    }

    public void onNewData(List<? extends SummeryBaseWrapperModel> items) {

        this.mList.clear();
        this.mList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        SummeryBaseWrapperModel wrapperModel = mList.get(position);

        switch (wrapperModel.getGravity()) {
            case Gravity.START:
                return HORIZONTAL;
            case Gravity.CENTER_VERTICAL:
                return VERTICAL;
        }

        return HORIZONTAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                ( viewType == VERTICAL )
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.row_summery_container_item, parent, false)
                : LayoutInflater.from(parent.getContext()).inflate(R.layout.row_summery_container_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SummeryBaseWrapperModel item = mList.get(position);
        holder.bindData(item);

        switch (item.getGravity()) {

            case Gravity.START:
            case Gravity.END:

                holder.rvList.setLayoutManager(new LinearLayoutManager(holder.rvList.getContext(), LinearLayoutManager.HORIZONTAL, false));

                if ( item instanceof PlayerSectionWrapperModel) {
                    //new com.github.rubensousa.gravitysnaphelper.GravitySnapHelper(Gravity.START).attachToRecyclerView(holder.rvList);
                    SummeryRvAdapter summeryRvAdapter = new SummeryRvAdapter(mContext, true, false, ((PlayerSectionWrapperModel) item).getPlayers());
                    holder.rvList.setAdapter(summeryRvAdapter);

                } else if ( item instanceof TeamSectionWrapperModel) {
                    //new com.github.rubensousa.gravitysnaphelper.GravitySnapHelper(Gravity.START).attachToRecyclerView(holder.rvList);
                    SummeryRvAdapter summeryRvAdapter = new SummeryRvAdapter(mContext, true, false, ((TeamSectionWrapperModel) item).getTeams());
                    holder.rvList.setAdapter(summeryRvAdapter);

                } else if ( item instanceof MatchSectionWrapperModel ) {
                    //new com.github.rubensousa.gravitysnaphelper.GravitySnapHelper(Gravity.START).attachToRecyclerView(holder.rvList);
                    SummeryRvAdapter summeryRvAdapter = new SummeryRvAdapter(mContext, true, false, ((MatchSectionWrapperModel) item).getMatches());
                    holder.rvList.setAdapter(summeryRvAdapter);
                } else if ( item instanceof CompetitionSectionWrapperModel) {
                    SummeryRvAdapter summeryRvAdapter = new SummeryRvAdapter(mContext, true, false, ((CompetitionSectionWrapperModel) item).getCompetitionList());
                    holder.rvList.setAdapter(summeryRvAdapter);
                }

                break;

            case Gravity.CENTER_VERTICAL:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    /*--------------------------------------------------------------------*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSummeryText;
        public RecyclerView rvList;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSummeryText = itemView.findViewById(R.id.tv_summery_title);
            rvList = itemView.findViewById(R.id.rv_list);
        }

        public void bindData(SummeryBaseWrapperModel item) {

            tvSummeryText.setText(item.getTitle());

        }

    }



}
