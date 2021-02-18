package com.ddastudio.hifivefootball_android.match_summery;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ddastudio.hifivefootball_android.match_summery.model.SummeryBaseWrapperModel;

import java.util.ArrayList;
import java.util.List;

public class MatchSummeryViewModel extends ViewModel {

    private MutableLiveData<List<SummeryBaseWrapperModel>> mList;

    public LiveData<List<SummeryBaseWrapperModel>> getSummeryList() {

        if ( mList == null ) {
            mList = new MutableLiveData<>();
            mList.setValue(new ArrayList<>());
        }

        return mList;
    }

    public void addItems(List<? extends SummeryBaseWrapperModel> items) {

        mList.getValue().addAll(items);
        mList.setValue(mList.getValue());
    }

    public void clearData() {

        if ( mList != null ) {

            if ( mList.getValue() != null ) {

                mList.getValue().clear();
                mList.setValue(mList.getValue());
            }
        }
    }
}
