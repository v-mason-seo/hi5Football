package com.ddastudio.hifivefootball_android.board.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 11. 30..
 */

@Parcel
@Entity(tableName = "boards")
public class BoardMasterModel implements MultiItemEntity{

    @Ignore
    int itemType;

    @PrimaryKey
    @ColumnInfo(name = "board_id")
    @SerializedName("board_id")
    int boardId;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    String title;

    @ColumnInfo(name = "subtitle")
    @SerializedName("subtitle")
    String subtitle;

    @ColumnInfo(name = "short_name")
    @SerializedName("short_name")
    String shortName;

    @ColumnInfo(name = "type_gb")
    @SerializedName("type_gb")
    String typeGb;

    @ColumnInfo(name = "group")
    @SerializedName("group")
    int group;

    @ColumnInfo(name = "group_name")
    @SerializedName("group_name")
    String groupName;

    @ColumnInfo(name = "img_url")
    @SerializedName("img_url")
    int imgUrl;

    @ColumnInfo(name = "has_best")
    @SerializedName("has_best")
    int hasBest;

    @ColumnInfo(name = "use_yn")
    @SerializedName("use_yn")
    int useYn;

    @ColumnInfo(name = "has_instance_text")
    @SerializedName("has_instance_text")
    int hasInstanceText;

    @ColumnInfo(name = "instance_text")
    @SerializedName("instance_text")
    String instanceText;

    // 현재 게시판이 선택되었는지 여부
    @ColumnInfo(name = "is_sel")
    int isSelected;
    // 베스트 게시판을 선택했는지 여부
    @ColumnInfo(name = "is_sel_best")
    int isSelectedBest;


    /*--------------------------------------
     * 생성자
     *--------------------------------------*/
    public void BoardMasterModel() {
        this.itemType = ViewType.BOARD;
        this.isSelected = 0;
        this.isSelectedBest = 0;
    }

    @Override
    public int getItemType() {
        return ViewType.BOARD;
    }

    @Ignore
    public int getBoardId() {
        return boardId;
    }

    @Ignore
    public String getTitle() {
        return title;
    }

    @Ignore
    public String getSubtitle() {
        return TextUtils.isEmpty(subtitle) ? "" : subtitle;
    }

    @Ignore
    public String getShortName() {
        return shortName;
    }

    @Ignore
    public String getTypeGb() {
        return typeGb;
    }

    @Ignore
    public int getGroup() {
        return group;
    }

    @Ignore
    public String getGroupName() {
        return groupName;
    }

    @Ignore
    public int getImgUrl() {
        return imgUrl;
    }

    @Ignore
    public int getHasBest() {
        return hasBest;
    }

    @Ignore
    public int getUseYn() {
        return useYn;
    }

    @Ignore
    public int getHasInstanceText() {
        return hasInstanceText;
    }

    @Ignore
    public String getInstanceText() {
        return TextUtils.isEmpty(instanceText) ? "" : instanceText;
    }

    @Ignore
    public int isSelected() {
        return isSelected;
    }

    @Ignore
    public void setSelected(int selected) {
        isSelected = selected;
    }

    @Ignore
    public int isSelectedBest() {
        return isSelectedBest;
    }

    @Ignore
    public void setSelectedBest(int selectedBest) {
        isSelectedBest = selectedBest;
    }


    /*-----------------------------------------------------------------------*/

    @Override
    public String toString() {

        if ( TextUtils.isEmpty(subtitle)) {
            return super.toString();
        } else {
            return getSubtitle();
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BoardMasterModel board = (BoardMasterModel) obj;

        return this.getBoardId() == board.getBoardId()
                && this.getGroup() == board.getGroup()
                && this.getHasBest() == board.getHasBest()
                && this.getHasInstanceText() == board.getHasInstanceText()
                && this.getInstanceText().equals(board.getInstanceText())
                && this.isSelected() == board.isSelected()
                && this.isSelectedBest() == board.isSelectedBest();
    }
}
