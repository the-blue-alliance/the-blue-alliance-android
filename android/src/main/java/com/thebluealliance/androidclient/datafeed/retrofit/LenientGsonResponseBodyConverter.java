package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.Reader;

import retrofit.Converter;

/**
 * A {@linkplain Converter} that uses a lenient gson {@link JsonReader}
 * Based off https://github.com/square/retrofit/blob/master/retrofit-converters/gson/src/main/java/retrofit2/converter/gson/GsonResponseBodyConverter.java
 */
public class LenientGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final TypeAdapter<T> adapter;

    public LenientGsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader in = value.charStream();

        try {
            JsonReader reader = new JsonReader(in);
            reader.setLenient(true);
            return adapter.read(reader);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}
