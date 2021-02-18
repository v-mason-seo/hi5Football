package com.ddastudio.hifivefootball_android.data.event;

/**
 * Created by hongmac on 2018. 2. 6..
 */

public class FixtureEvent {

    /**
     * MatchDetailInfoActivity에서 FixtureFragmet로 보내는 이벤트
     * 특정 리그를 선택하고 완료했을 때 보내며, 이 이벤트를 받으면 데이터를 다시 불러온다.
     */
    public static class RefreshFixtureEvent {

        String compList;

        public RefreshFixtureEvent(String compList) {
            this.compList = compList;
        }

        public String getCompList() {
            return compList;
        }

        public void setCompList(String compList) {
            this.compList = compList;
        }
    }
}
