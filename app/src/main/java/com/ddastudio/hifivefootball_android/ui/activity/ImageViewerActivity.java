package com.ddastudio.hifivefootball_android.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageViewerActivity extends BaseActivity {

    @BindView(R.id.firstImage) SimpleDraweeView iv1;
    @BindView(R.id.secondImage) SimpleDraweeView iv2;
    @BindView(R.id.thirdImage) SimpleDraweeView iv3;

    protected String[] posters, descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        posters = getPosters();
        descriptions = getDescriptions();


        iv1.setImageURI(posters[0]);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(posters[6])
                .setAutoPlayAnimations(true)
                .build();
        //iv2.setImageURI(posters[1]);
        iv2.setController(controller);

        iv3.setImageURI(posters[2]);

    }

    @OnClick({R.id.firstImage, R.id.secondImage, R.id.thirdImage})
    public void onClick(View view) {

        int startPosition = 0;
        switch (view.getId()) {
            case R.id.firstImage:
                startPosition = 0;
                break;
            case R.id.secondImage:
                startPosition = 1;
                break;
            case R.id.thirdImage:
                startPosition = 2;
                break;
        }
        new ImageViewer.Builder<>(this, posters)
                .setStartPosition(startPosition)
                .show();
    }

//    private void initDrawee(SimpleDraweeView drawee, final int startPosition) {
//        drawee.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPicker(startPosition);
//            }
//        });
//        drawee.setImageURI(posters[startPosition]);
//    }


    private static final String POSTERS_PATH = "https://raw.githubusercontent.com/stfalcon-studio/FrescoImageViewer/v.0.5.0/images/posters";

    public static String[] getPosters() {
        return new String[]{
                POSTERS_PATH + "/Vincent.jpg",
                "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/arena/20180326_021205_336_oopk.gif",
                POSTERS_PATH + "/Korben.jpg",
                "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/arena/20180326_021205_336_oopk.gif",
                POSTERS_PATH + "/Marty.jpg",
                POSTERS_PATH + "/Driver.jpg",
                "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/arena/20180326_022122_372_oopk.gif",
                POSTERS_PATH + "/Max.jpg",
                POSTERS_PATH + "/Daniel.jpg"
        };
    }

    public static String[] getDescriptions() {
        return new String[]{
                "Vincent Vega is a hitman and associate of Marsellus Wallace. He had a brother named Vic Vega who was shot and killed by an undercover cop while on a job. He worked in Amsterdam for over three years and recently returned to Los Angeles, where he has been partnered with Jules Winnfield.",
                "Jules Winnfield - initially he is a Hitman working alongside Vincent Vega but after revelation, or as he refers to it \"a moment of clarity\" he decides to leave to \"Walk the Earth.\" During the film he is stated to be from Inglewood, California",
                "Korben Dallas. A post-America taxi driver in New York City with a grand military background simply lives his life day to day, that is, before he meets Leeloo. Leeloo captures his heart soon after crashing into his taxi cab one day after escaping from a government-run laboratory. Korben soon finds himself running from the authorities in order to protect Leeloo, as well as becoming the center of a desperate ploy to save the world from an unknown evil.",
                "Dominic \"Dom\" Toretto is the brother of Mia Toretto, uncle to Jack and husband to Letty Ortiz. The protagonist in The Fast and the Furious franchise, Dominic is an elite street racer and auto mechanic.",
                "Martin Seamus \"Marty\" McFly Sr. - he is the world's second time traveler, the first to travel backwards in time and the first human to travel though time. He was also a high school student at Hill Valley High School in 1985. He is best friends with Dr. Emmett Brown, who unveiled his first working invention to him.",
                "The Driver - real name unknown - is a quiet man who has made a career out of stealing fast cars and using them as getaway vehicles in big-time robberies all over Los Angeles. Hot on the Driver's trail is the Detective (Bruce Dern), a conceited (and similarly nameless) cop who refers to the Driver as \"Cowboy\".",
                "Frank Martin (Transporter) - he initially serves as a reluctant hero. He is portrayed as a former Special Forces operative who was a team leader of a search and destroy unit. His military background includes operations \"in and out of\" Lebanon, Syria and Sudan. He retires from this after becoming fatigued and disenchanted with his superior officers.",
                "Maximillian \"Max\" Rockatansky started his apocalyptic adventure as a Main Force Patrol officer who fought for peace on the decaying roads of Australian civilization. Max served as the last line of defense against the reckless marauders terrorizing the roadways, driving a V8 Interceptor.",
                "Daniel Morales - the fastest delivery man for the local pizza parlor Pizza Joe in Marseille, France. On the last day of work, he sets a new speed record, then leaves the job to pursue a new career as a taxi driver with the blessings of his boss and co-workers. Daniel's vehicle is a white 1997 Peugeot 406..."
        };
    }

    public static List<CustomImage> getCustomImages() {
        String[] posters = getPosters();
        String[] descriptions = getDescriptions();
        List<CustomImage> images = new ArrayList<>();

        for (int i = 0; i < posters.length; i++) {
            images.add(new CustomImage(posters[i], descriptions[i]));
        }

        return images;
    }

    public static class CustomImage {

        private String url;
        private String description;

        public CustomImage(String url, String description) {
            this.url = url;
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public String getDescription() {
            return description;
        }
    }
}
