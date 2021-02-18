package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;

public class SelectedSiteEvent {

    SiteBoardModel siteInfo;

    public SelectedSiteEvent(SiteBoardModel site) {
        this.siteInfo = site;
    }

    public SiteBoardModel getSiteInfo() {
        return siteInfo;
    }

}
