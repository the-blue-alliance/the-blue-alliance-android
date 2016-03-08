/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSON
 * Modified from https://github.com/square/retrofit/blob/master/retrofit-converters/gson/src/main/java/retrofit/GsonConverterFactory.java
 */
public final class LenientGsonConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON
     * and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static LenientGsonConverterFactory create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static LenientGsonConverterFactory create(Gson gson) {
        return new LenientGsonConverterFactory(gson);
    }

    private final Gson gson;

    private LenientGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new LenientGsonRequestBodyConverter<>(adapter);
    }

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new LenientGsonResponseBodyConverter<>(adapter);
    }
}
