package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.Constants;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.mime.TypedByteArray;
import rx.Observable;

/**
 * Created by phil on 3/28/15.
 */
public interface APIv2 {

    /* Team List */
    @GET("/{endpoint}")
    public Observable<String> endpoint(
            @Path("endpoint") String endpoint,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    static class APIv2RequestInterceptor implements RequestInterceptor {

        /**
         * ONLY SET THIS VARIABLE FROM WITHIN TESTS
         * It's so we can send a different App Id
         */
        public static boolean isFromJunit = false;

        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-TBA-App-Id", Constants.getApiHeader()+(isFromJunit?"-junit":""));
        }
    }

    static class APIv2ErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            System.out.println(cause);
            Response response = cause.getResponse();
            if(response != null) {
                TypedByteArray data = (TypedByteArray) (response.getBody());
                byte[] bytes = data.getBytes();
                System.out.println(new String(bytes));
            }
            return cause;
        }
    }
}
