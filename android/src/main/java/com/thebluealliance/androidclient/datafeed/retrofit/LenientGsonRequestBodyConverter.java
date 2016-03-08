package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.TypeAdapter;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import okio.Buffer;
import retrofit.Converter;

/**
 * A {@linkplain Converter} that uses lenient gson, paired with {@link LenientGsonResponseBodyConverter}
 * Based off https://github.com/square/retrofit/blob/master/retrofit-converters/gson/src/main/java/retrofit2/converter/gson/GsonRequestBodyConverter.java
 */
public class LenientGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final TypeAdapter<T> adapter;

    public LenientGsonRequestBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        try {
            adapter.toJson(writer, value);
            writer.flush();
        } catch (IOException e) {
            throw new AssertionError(e); // Writing to Buffer does no I/O.
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}
