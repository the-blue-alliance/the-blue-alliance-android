package com.thebluealliance.androidclient.datafeed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Single;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ObservableCompletionAggregatorTest {

    /**
     * If all the Observables being tracked by a  started {@link ObservableCompletionAggregator}
     * complete without errors, the {@link ObservableCompletionAggregator} should complete as well.
     */
    @Test
    public void testAllSuccessful() {
        Single<Void> single1 = Single.just(null);
        Single<Void> single2 = Single.just(null);
        Single<Void> single3 = Single.just(null);

        ObservableCompletionAggregator agg = new ObservableCompletionAggregator();
        agg.addSingle(single1);
        agg.addSingle(single2);
        agg.addSingle(single3);
        agg.start();

        TestSubscriber sub = new TestSubscriber();
        agg.getObservable().subscribe(sub);

        sub.assertCompleted();
    }

    /**
     * If {@link ObservableCompletionAggregator#start()} is not called, the
     * {@link ObservableCompletionAggregator} should not complete, even if all the observables it's
     * tracking do complete.
     */
    @Test
    public void testFailureIfNotStarted() {
        Single<Void> single1 = Single.just(null);
        Single<Void> single2 = Single.just(null);
        Single<Void> single3 = Single.just(null);

        ObservableCompletionAggregator agg = new ObservableCompletionAggregator();
        agg.addSingle(single1);
        agg.addSingle(single2);
        agg.addSingle(single3);

        TestSubscriber sub = new TestSubscriber();
        agg.getObservable().subscribe(sub);

        sub.assertNoErrors();
        sub.assertNotCompleted();
    }

    /**
     * If any of the Observables being tracked by a started {@link ObservableCompletionAggregator}
     * complete with an error, the {@link ObservableCompletionAggregator} should pass on the error.
     */
    @Test
    public void testOneErrored() {
        Single<Void> single1 = Single.just(null);
        Single<Void> single2 = Single.error(new Exception());
        Single<Void> single3 = Single.just(null);

        ObservableCompletionAggregator agg = new ObservableCompletionAggregator();
        agg.addSingle(single1);
        agg.addSingle(single2);
        agg.addSingle(single3);
        agg.start();

        TestSubscriber sub = new TestSubscriber();
        agg.getObservable().subscribe(sub);

        sub.assertError(Throwable.class);
    }

    /**
     * Adding an observable to an {@link ObservableCompletionAggregator} after it has been started
     * should have no effect on the aggregator. It should finish normally once all the observables
     * added prior to start have completed.
     */
    @Test
    public void testAddObservableAfterStart() {
        Subject subject1 = PublishSubject.create();
        Subject subject2 = PublishSubject.create();
        Subject subject3 = PublishSubject.create();
        Subject subject4 = PublishSubject.create();

        ObservableCompletionAggregator agg = new ObservableCompletionAggregator();
        agg.addObservable(subject1);
        agg.addObservable(subject2);
        agg.addObservable(subject3);
        agg.start();
        agg.addObservable(subject4);

        TestSubscriber sub = new TestSubscriber();
        agg.getObservable().subscribe(sub);

        subject1.onNext(null);
        subject1.onCompleted();
        subject2.onNext(null);
        subject2.onCompleted();
        subject3.onNext(null);
        subject3.onCompleted();

        sub.assertCompleted();
    }
}
