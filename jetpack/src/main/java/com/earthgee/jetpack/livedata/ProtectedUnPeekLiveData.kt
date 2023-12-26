package com.earthgee.jetpack.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.Objects
import java.util.concurrent.atomic.AtomicInteger

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
open class ProtectedUnPeekLiveData<T>(value: T?) : LiveData<T>(value) {

    companion object {
        const val START_VERSION = -1
    }

    private val mCurrentVersion = AtomicInteger(START_VERSION)

    /**
     * TODO 当 liveData 用作 event 时，可使用该方法观察 "生命周期敏感" 非粘性消息
     *
     * @param owner    activity 传入 this，fragment 建议传入 getViewLifecycleOwner
     * @param observer observer
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, createObserverWrapper(observer, mCurrentVersion.get()))
    }

    /**
     * TODO 当 liveData 用作 event 时，可使用该方法观察 "生命周期不敏感" 非粘性消息
     *
     * @param observer observer
     */
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(createObserverWrapper(observer, mCurrentVersion.get()))
    }

    /**
     * TODO 当 liveData 用作 state 时，可使用该方法来观察 "生命周期敏感" 粘性消息
     *
     * @param owner    activity 传入 this，fragment 建议传入 getViewLifecycleOwner
     * @param observer observer
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, createObserverWrapper(observer, START_VERSION))
    }

    /**
     * TODO 当 liveData 用作 state 时，可使用该方法来观察 "生命周期不敏感" 粘性消息
     *
     * @param observer observer
     */
    fun observeStickyForever(observer: Observer<in T>) {
        super.observeForever(createObserverWrapper(observer, START_VERSION))
    }

    /**
     * TODO tip：只需重写 setValue
     * postValue 最终还是会经过这里
     *
     * @param value value
     */
    override fun setValue(value: T) {
        mCurrentVersion.getAndIncrement()
        super.setValue(value)
    }

    /**
     * TODO tip：
     * 1.添加一个包装类，自己维护一个版本号判断，用于无需 map 帮助也能逐一判断消费情况
     * 2.重写 equals 方法和 hashCode，在用于手动 removeObserver 时，忽略版本号的变化引起的变化
     */
    inner class ObserverWrapper(
        private val mObserver: Observer<in T>,
        private val mVersion: Int = START_VERSION
    ) : Observer<T> {

        override fun onChanged(value: T) {
            if (mCurrentVersion.get() > mVersion) {
                mObserver.onChanged(value)
            }
        }

        override fun hashCode(): Int {
            return Objects.hash(mObserver)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (other == null || javaClass != other.javaClass) {
                return false
            }
            val that = other as ProtectedUnPeekLiveData<*>.ObserverWrapper
            return mObserver === that.mObserver
        }

    }

    private fun createObserverWrapper(observer: Observer<in T>, version: Int) =
        ObserverWrapper(observer, version)

    /**
     * TODO tip：
     * 通过 ObserveForever Observe，需记得 remove，不然存在 LiveData 内存泄漏隐患，
     * 保险做法是，在页面 onDestroy 环节安排 removeObserver 代码，
     * 具体可参见 app module ObserveForeverFragment 案例
     *
     * @param observer observeForever 注册的 observer，或 observe 注册的 observerWrapper
     */
    fun removeObserverWrapper(observer: Observer<in T>) {
        if (observer.javaClass.isAssignableFrom(ObserverWrapper::class.java)) {
            super.removeObserver(observer)
        } else {
            super.removeObserver(createObserverWrapper(observer, START_VERSION))
        }
    }

    /**
     * TODO tip：
     * 手动将消息从内存中清空，
     * 以免无用消息随着 SharedViewModel 长时间驻留而导致内存溢出发生。
     */
    fun clean() {
        super.setValue(null)
    }

}