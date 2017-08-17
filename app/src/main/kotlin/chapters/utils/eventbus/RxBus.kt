package chapters.utils.eventbus

import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject


object RxBus {
    private val bus = SerializedSubject<Any, Any>(PublishSubject.create())

    fun send(any: Any?) {
        bus.onNext(any)
    }

    fun toObservable() = bus

    fun <T> observeEvent(events: Class<T>) = bus.ofType(events)

}