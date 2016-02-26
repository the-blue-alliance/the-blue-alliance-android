package com.thebluealliance.androidclient.imgur;

import com.google.gson.Gson;

import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.imgur.ImgurApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;

@Module(includes = HttpModule.class)
public class ImgurModule {

    public ImgurModule() {}

    @Provides @Singleton @Named("imgur_retrofit")
    public Retrofit provideImgurRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ImgurApi.SERVER_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .build();
    }

    @Provides @Singleton
    public ImgurApi provideImgurApi(@Named("imgur_retrofit") Retrofit retrofit) {
        return retrofit.create(ImgurApi.class);
    }

    @Provides @Singleton
    public ImgurController provideImgurController(ImgurApi api) {
        return new ImgurController(api);
    }
}
