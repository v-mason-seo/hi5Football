package com.ddastudio.hifivefootball_android.football_chat.api;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.football_chat.repo.ApiConstants;
import com.ddastudio.hifivefootball_android.football_chat.utils.LiveDataCallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatServiceFactory {

    private static volatile OkHttpClient client;

    private static volatile ChatService service;

    @NonNull
    public static ChatService getService() {
        ChatService service = ChatServiceFactory.service;
        if (service == null) {
            synchronized (ChatServiceFactory.class) {
                service = ChatServiceFactory.service;
                if (service == null) {
                    service = ChatServiceFactory.service = createService();
                }
            }
        }
        return service;
    }

    @NonNull
    private static ChatService createService() {

        return new Retrofit.Builder()
                .baseUrl(ApiConstants.HIFIVE_SERVER_BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ChatService.class);
    }

    @NonNull
    private static OkHttpClient getClient() {
        OkHttpClient client = ChatServiceFactory.client;
        if (client == null) {
            synchronized (ChatServiceFactory.class) {
                client = ChatServiceFactory.client;
                if (client == null) {
                    client = ChatServiceFactory.client = buildClient();
                }
            }
        }
        return client;
    }

    @NonNull
    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                //.addInterceptor(new ApiKeyInterceptor())
                .build();
    }
}
