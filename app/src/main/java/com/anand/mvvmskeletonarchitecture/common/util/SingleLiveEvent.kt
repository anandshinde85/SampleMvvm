package com.anand.mvvmskeletonarchitecture.common.util

import android.util.ArrayMap
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 * Issue with using normal MutableLiveData is that, whenever a new observer gets attached to the MutableLiveData it
 * emits the last present value to the newly attached observer, this causes many problems in case of configuration changes
 * due to screen rotation
 *
 * This class is an extension of MutableLiveData which notifies the observers new value when there is
 * an explicit call to setValue() or call()
 *
 */

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pendingFlagsMap = ArrayMap<Observer<T>, AtomicBoolean>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val innerObserver = object : Observer<T> {
            override fun onChanged(t: T) {
                if (pendingFlagsMap.contains(this) && pendingFlagsMap.getValue(this).compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        }

        // Observe the internal MutableLiveData
        pendingFlagsMap[innerObserver] = AtomicBoolean(false)
        super.observe(owner, innerObserver)
    }

    @MainThread
    override fun setValue(t: T?) {
        for ((_, pending) in pendingFlagsMap)
            pending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    override fun removeObserver(observer: Observer<in T>) {
        pendingFlagsMap.remove(observer)
        super.removeObserver(observer)
    }

    fun clear() {
        pendingFlagsMap.clear()
    }
}