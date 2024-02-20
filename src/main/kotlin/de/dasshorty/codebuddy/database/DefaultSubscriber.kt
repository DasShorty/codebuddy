package de.dasshorty.codebuddy.database

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.function.Consumer


@JvmRecord
data class DefaultSubscriber<T>(val request: Long, val consumer: Consumer<T?>) :
    Subscriber<T> {
    override fun onSubscribe(subscription: Subscription) {
        subscription.request(this.request)
    }

    override fun onNext(t: T) {
        consumer.accept(t)
    }

    override fun onError(throwable: Throwable) {
        throwable.fillInStackTrace()
        consumer.accept(null)
    }

    override fun onComplete() {
    }
}