package com.ddastudio.hifivefootball_android.content_issue;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IssueContainerFragemtn extends Fragment {


    public IssueContainerFragemtn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issue_container_fragemtn, container, false);
    }

}
