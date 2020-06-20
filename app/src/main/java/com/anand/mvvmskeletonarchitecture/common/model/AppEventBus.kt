package com.anand.mvvmskeletonarchitecture.common.model

import com.anand.mvvmskeletonarchitecture.common.util.SingleLiveEvent

/**
 * Class to emit/observe events within application. To be used as event bus for
 */
//@Singleton
//class SimpleLiveDataEventBus @Inject constructor() {
class AppEventBus() {

    val eventLiveData = SingleLiveEvent<Event<*>>()
}

enum class EventType {
    // All events to be added here as enum
}

data class Event<T>(val eventType: EventType, val data: T)