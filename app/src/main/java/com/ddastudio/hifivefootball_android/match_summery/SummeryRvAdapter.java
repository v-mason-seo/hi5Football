package com.ddastudio.hifivefootball_android.match_summery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionModel;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SummeryRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public final static int PLAYER = 0;
    public final static int TEAM = 1;
    public final static int MATCH = 2;
    public final static int COMPETITION = 3;

    private List<Object> mApps;
    private boolean mHorizontal;
    private boolean mPager;
    private Context mContext;


    public SummeryRvAdapter(Context context, boolean horizontal, boolean pager, List<? extends Object> apps) {
        mContext = context;
        mHorizontal = horizontal;
        mApps = new ArrayList<>();
        mApps.addAll(apps);
        mPager = pager;
    }


    @Override
    public int getItemViewType(int position) {

        if ( mApps.get(position) instanceof MatchModel) {
            return MATCH;
        } else if ( mApps.get(position) instanceof PlayerModel) {
            return PLAYER;
        } else if ( mApps.get(position) instanceof TeamModel) {
            return TEAM;
        } else if (mApps.get(position) instanceof CompModel) {
            return COMPETITION;
        } else {
            return PLAYER;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case MATCH:
                return new MatchViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_summery_match_item, parent, false));

            case PLAYER:
                return new PlayerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_summery_player_item, parent, false));

            case TEAM:
                return new TeamViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_summery_team_item, parent, false));

            case COMPETITION:
                return new CompetitionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_summery_competition_item, parent, false));
            default:
                return null;
        }

        /*
        if (mPager) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_snap_pager_item, parent, false));
        } else {
            return mHorizontal ? new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_snap_player_item, parent, false)) :
                    new ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.row_snap_vertical_item, parent, false));
        }*/
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case PLAYER:
                PlayerModel player = (PlayerModel)mApps.get(position);
                PlayerViewHolder playerViewHolder = (PlayerViewHolder)holder;
                // 데이터 바인딩
                playerViewHolder.bindData(player);

                //------------------------------------
                // 플레이어 아바타
                //------------------------------------
                GlideApp.with(mContext)
                        .load(player.getPlayerLargeImageUrl())
                        .centerCrop()
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_person_grey_vector)
                        .into(playerViewHolder.ivPlayerAvatar);

                //------------------------------------
                // 팀 엠블럼
                //------------------------------------
                GlideApp.with(mContext)
                        .load(player.getSmallEmblemUrl())
                        .fitCenter()
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(playerViewHolder.ivTeamEmblem2);

                break;
            case TEAM:

                TeamViewHolder teamViewHolder = (TeamViewHolder)holder;
                TeamModel team = (TeamModel)mApps.get(position);
                // 데이터 바인딩
                teamViewHolder.bindData(team);

                //------------------------------------
                // 팀 엠블럼
                //------------------------------------
                GlideApp.with(mContext)
                        .load(team.getLargeEmblemUrl())
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(2, 2))
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(teamViewHolder.ivTeamEmblem);
                break;

            case MATCH:

                MatchViewHolder matchViewHolder = (MatchViewHolder)holder;
                MatchModel matchModel = (MatchModel)mApps.get(position);
                // 데이터 바인딩
                matchViewHolder.bindData(matchModel);


                //------------------------------------
                // 컴피티션
                //------------------------------------
                GlideApp.with(mContext)
                        .load(matchModel.getCompetitionSmallImage())
                        .fitCenter()
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(matchViewHolder.ivComp);

                //------------------------------------
                // 홈팀 엠블럼
                //------------------------------------
                GlideApp.with(mContext)
                        .load(matchModel.getHomeTeamLargeEmblemUrl())
                        .fitCenter()
                        //.transform(new CircleCrop())
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(matchViewHolder.ivHomeTeamEmblem);

                //------------------------------------
                // 어웨이팀 엠블럼
                //------------------------------------
                GlideApp.with(mContext)
                        .load(matchModel.getAwayTeamLargeEmblemUrl())
                        .fitCenter()
                        //.transform(new CircleCrop())
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(matchViewHolder.ivAwayTeamEmblem);

                break;

            case COMPETITION:
                CompetitionViewHolder competitionViewHolder = (CompetitionViewHolder)holder;
                CompModel comp = (CompModel)mApps.get(position);
                competitionViewHolder.bindData(comp);

                //------------------------------------
                // 리그 로고
                //------------------------------------
                GlideApp.with(mContext)
                        .load(comp.getCompetitionLargeImageUrl())
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(2, 2))
                        .placeholder(R.drawable.ic_circle_grey_vector)
                        .into(competitionViewHolder.ivCompetition);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mApps.size();
    }


    /*-------------------------------------------------------------------------------------*/



    /*---------------------------------------
     * 매치 뷰홀더
     *---------------------------------------*/
    public class MatchViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llHeader;
        ImageView ivComp;
        TextView tvCompName;
        TextView tvMatchDate;

        TextView tvHomeTeamName;
        TextView tvAwayTeamName;

        ImageView ivHomeTeamEmblem;
        ImageView ivAwayTeamEmblem;

        TextView tvHomeTeamScore;
        TextView tvAwayTeamScore;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            llHeader = itemView.findViewById(R.id.ll_header);
            ivComp = itemView.findViewById(R.id.iv_comp);
            tvCompName = itemView.findViewById(R.id.tv_comp_name);
            tvMatchDate = itemView.findViewById(R.id.tv_match_date);


            tvHomeTeamName = itemView.findViewById(R.id.tv_home_team_name);
            tvAwayTeamName = itemView.findViewById(R.id.tv_away_team_name);

            ivHomeTeamEmblem = itemView.findViewById(R.id.iv_home_team_image);
            ivAwayTeamEmblem = itemView.findViewById(R.id.iv_away_team_image);

            tvHomeTeamScore = itemView.findViewById(R.id.tv_home_team_score);
            tvAwayTeamScore = itemView.findViewById(R.id.tv_away_team_score);

            // 1. 전체클릭
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MatchModel match = (MatchModel)mApps.get(position);
                    if ( match != null ) {
                        openMatchActivity(mContext, match);
                    }
                }
            });

            // 2. 홈팀 이미지 클릭
            ivHomeTeamEmblem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MatchModel match = (MatchModel)mApps.get(position);
                    if ( match != null ) {
                        openTeamActivity(mContext, match.getHomeTeamId());
                    }
                }
            });

            // 3. 어웨이팀 클릭
            ivAwayTeamEmblem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MatchModel match = (MatchModel)mApps.get(position);
                    if ( match != null ) {
                        openTeamActivity(mContext, match.getAwayTeamId());
                    }
                }
            });

        }

        public void bindData(MatchModel matchModel) {

            if ( matchModel == null )
                return;

            //int headerBackColor = ColorGenerator.MATERIAL.getColor(matchModel.getCompetitionId());
            //llHeader.setBackgroundColor(headerBackColor);
            llHeader.setBackgroundResource(R.color.md_blue_grey_200);
            tvCompName.setText(matchModel.getCompetitionName());
            tvMatchDate.setText(matchModel.getMatchDateTime());

            tvHomeTeamName.setText(matchModel.getHomeTeamName());
            tvAwayTeamName.setText(matchModel.getAwayTeamName());

            tvHomeTeamScore.setText(matchModel.getHomeTeamScoreString());
            tvAwayTeamScore.setText(matchModel.getAwayTeamScoreString());
        }
    }


    /*---------------------------------------
     * 플레이어 뷰홀더
     *---------------------------------------*/
    public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout llHeader;
        ImageView ivTeamEmblem2;
        TextView tvTeamName;

        public ImageView ivPlayerAvatar;
        public TextView tvPlayerName;
        public TextView tvHits;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            llHeader = itemView.findViewById(R.id.ll_header);
            ivTeamEmblem2 = itemView.findViewById(R.id.iv_team_emblem2);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);

            ivPlayerAvatar = itemView.findViewById(R.id.iv_player_avatar);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvHits = itemView.findViewById(R.id.tv_hits);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    PlayerModel player = (PlayerModel)mApps.get(position);
                    if ( player != null ) {
                        openPlayerActivity(mContext, player.getPlayerId(), player.getPlayerName());
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Log.d("App", mApps.get(getAdapterPosition()).getName());
        }

        public void bindData(PlayerModel player) {

            if ( player == null )
                return;

            if (!TextUtils.isEmpty(player.getTeamName()) && player.getTeamName().length() > 0) {
                int headerBackColor = ColorGenerator.MATERIAL.getColor(player.getTeamName().substring(0, 1));
                llHeader.setBackgroundColor(headerBackColor);
            }
            tvTeamName.setText(player.getTeamName());

            tvPlayerName.setText(player.getPlayerName());
            tvHits.setText("HIT : " + player.getHitString());
        }
    }


    /*---------------------------------------
     * 팀 뷰홀더
     *---------------------------------------*/
    public class TeamViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivTeamEmblem;
        public TextView tvTeamName;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTeamEmblem = itemView.findViewById(R.id.iv_team_emblem);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TeamModel team = (TeamModel)mApps.get(position);
                    if ( team != null ) {
                        openTeamActivity(mContext, team.getTeamId());
                    }
                }
            });
        }

        public void bindData(TeamModel team) {

            if ( team == null )
                return;

            tvTeamName.setText(team.getTeamName());
        }
    }

    public class CompetitionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_competition)
        ImageView ivCompetition;

        @BindView(R.id.tv_competition_name)
        TextView tvCompetitionName;

        public CompetitionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(CompModel comp) {

            tvCompetitionName.setText(comp.getCompetitionName());
        }
    }

    /*----------------------------
     * 매치 액티비티 실행
     * @param context
     * @param match
     *----------------------------*/
    public void openMatchActivity(Context context, MatchModel match) {
        Intent intent = new Intent(context, MatchActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));

        context.startActivity(intent);
    }


    /*----------------------------
     * 팀 프로필 액티비티 실행
     * @param context
     * @param teamId
     *----------------------------*/
    public void openTeamActivity(Context context, int teamId) {
        Intent intent = new Intent(context, TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        context.startActivity(intent);
    }


    /*----------------------------
     * 플레이어 프로필 액티비티 실행
     * @param context
     * @param playerId
     * @param playerName
     *----------------------------*/
    public void openPlayerActivity(Context context, int playerId, String playerName) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);

        context.startActivity(intent);
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
}
