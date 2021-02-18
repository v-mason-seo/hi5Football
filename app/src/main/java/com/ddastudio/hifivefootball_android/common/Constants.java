package com.ddastudio.hifivefootball_android.common;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class Constants {
    /*
    * You should replace these values with your own. See the README for details
    * on what to fill in.
    */
    public static final String COGNITO_POOL_ID = "ap-northeast-2:7ab34e22-4d8a-405a-b391-3bcc3327420b";
    //public static final String COGNITO_POOL_ID = "5fb93156-bf38-4489-a85f-c3065b99f5d7";
    //public static final String COGNITO_POOL_ID = "ap-northeast-2:49cd767f-0035-4efc-b3a1-45c3cf8aff59";

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    //public static final String UPLOAD_POST_ORIGINAL_BUCKET_NAME = "hifivesoccer/original/post/170802";
    public static final String UPLOAD_POST_ORIGINAL_BUCKET_NAME = "hifivesoccer/origin/article";
    public static final String UPLOAD_PROFILE_ORIGINAL_BUCKET_NAME = "hifivesoccer/origin/profile";
    public static final String UPLOAD_POST_THUMBNAIL_BUCKET_NAME = "hifivesoccer/thumbnail/post/170802";
    public static final String UPLOAD_ARENA_ORIGINAL_BUCKET_NAME = "hifivesoccer/origin/arena";
}
