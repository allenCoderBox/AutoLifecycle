package com.wingjay.autolifecycle.library;

import android.support.annotation.NonNull;
import android.util.Log;
import rx.Observable;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * LifecycleProviderDelegate
 *
 * @author wingjay
 * @date 2017/10/17
 */
public class LifecycleProviderDelegate {

    public <T> Transformer<T, T> bindUntilEvent(@NonNull final PublishSubject<IContextLifecycle> lifecycleSubject,
                                                @NonNull final IContextLifecycle event) {
        ALog.i("bindUntilEvent " + event);
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> sourceObservable) {
                Observable<IContextLifecycle> o =
                    lifecycleSubject.takeFirst(new Func1<IContextLifecycle, Boolean>() {
                        @Override
                        public Boolean call(IContextLifecycle contextLifecycle) {
                            ALog.i(event + "appears!");
                            return contextLifecycle.equals(event);
                        }
                    });
                return sourceObservable.takeUntil(o);
            }
        };
    }

    public void executeWhen(@NonNull final PublishSubject<IContextLifecycle> lifecycleSubject,
                            @NonNull final Observable observable,
                            @NonNull final IContextLifecycle event) {
        lifecycleSubject.filter(new Func1<IContextLifecycle, Boolean>() {
            @Override
            public Boolean call(IContextLifecycle contextLifecycle) {
                return contextLifecycle.equals(event);
            }
        }).subscribe(new Subscriber<IContextLifecycle>() {
            @Override public void onCompleted() {
                ALog.d("executeWhen onComplete");
            }
            @Override public void onError(Throwable e) {
                ALog.d("executeWhen onError");
            }
            @Override public void onNext(IContextLifecycle iContextLifecycle) {
                observable.subscribe();
            }
        });
    }
}
