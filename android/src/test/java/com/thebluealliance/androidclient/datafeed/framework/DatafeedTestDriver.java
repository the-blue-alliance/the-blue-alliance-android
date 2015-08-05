package com.thebluealliance.androidclient.datafeed.framework;

import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DatafeedTestDriver {

    public static <API, VIEW> SubscriberTestController<API, VIEW> getSubscriberController(
      BaseAPISubscriber<API, VIEW> subscriber) {
        return new SubscriberTestController<>(subscriber);
    }

    public static <API, VIEW> void parseNullData(BaseAPISubscriber<API, VIEW> subscriber)
      throws BasicModel.FieldNotDefinedException {
        SubscriberTestController<API, VIEW> controller = getSubscriberController(subscriber);
        controller
          .withApiData(null)
          .parse()
          .bind();
    }

    public static <API, VIEW> void testSimpleParsing(
      BaseAPISubscriber<API, VIEW> subscriber,
      API data) throws BasicModel.FieldNotDefinedException {
        DataConsumer<VIEW> consumer = mock(DataConsumer.class);
        SubscriberTestController<API, VIEW> controller = getSubscriberController(subscriber);
        controller = controller
          .withConsumer(consumer)
          .onNext(data);

        verify(controller.getSubscriber()).parseData();
        verify(controller.getSubscriber()).bindData();
        verify(consumer).updateData(controller.getParsedData());

        controller = controller.complete();

        verify(controller.getSubscriber()).onCompleted();
        verify(consumer).onComplete();
    }

    public static <API, VIEW> VIEW getParsedData(
      BaseAPISubscriber<API, VIEW> subscriber,
      API data) throws BasicModel.FieldNotDefinedException {
        SubscriberTestController<API, VIEW> controller = getSubscriberController(subscriber);
        return controller
          .withApiData(data)
          .parse()
          .getParsedData();
    }
}
