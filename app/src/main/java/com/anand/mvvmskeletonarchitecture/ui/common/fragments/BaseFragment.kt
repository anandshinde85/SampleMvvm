package com.anand.mvvmskeletonarchitecture.ui.common.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anand.mvvmskeletonarchitecture.MainActivity
import com.anand.mvvmskeletonarchitecture.R
import com.anand.mvvmskeletonarchitecture.common.CustomApplication
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.CompositionRoot
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.Injector
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.PresentationCompositionRoot
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.Service
import com.anand.mvvmskeletonarchitecture.common.model.AppEventBus
import kotlinx.android.synthetic.main.fragment_base_layout.view.*
import java.lang.RuntimeException

abstract class BaseFragment : Fragment() {

    val TAG: String = javaClass.simpleName

    @Service
    lateinit var preferences: SharedPreferences

    @Service
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Service
    lateinit var appEventBus: AppEventBus
    var isAnim = false
    var isAnimationFinished = false

    private lateinit var compositionRoot: PresentationCompositionRoot

    private var mIsInjectorUsed : Boolean = false

    internal fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    protected fun getInjector() : Injector {
        if(mIsInjectorUsed){
            throw RuntimeException("No need to use injector more than once!")
        }
        mIsInjectorUsed = true
        return Injector(getCompositionRoot())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_base_layout, container, false)
        val content = inflater.inflate(getLayoutResourceId(), container, false)
        rootView.fragmentFrame.addView(content)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle()
    }

    /*
    * Use this function to load any data on screen after fragment animation is finished.
    * */
    open fun showContentAfterAnim() {}

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation: Animation?
        if (nextAnim == 0 || !enter) {
            animation = super.onCreateAnimation(transit, enter, nextAnim)
        } else {

            animation = AnimationUtils.loadAnimation(activity, nextAnim)
            animation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    isAnim = true
                    isAnimationFinished = false
                }

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    isAnimationFinished = true
                    if (enter) {
                        showContentAfterAnim()
                    }
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            })
        }
        return animation
    }

    /**
     * Override this method to supply layout id.
     */
    abstract fun getLayoutResourceId(): Int

    open fun setActivityTitle() {
        // Implementation is explicitly left out, derived classes to override
    }


    override fun onDestroyView() {
        appEventBus.eventLiveData.removeObservers(this)
        super.onDestroyView()
    }

    fun getCompositionRoot(): PresentationCompositionRoot {
        if (!::compositionRoot.isInitialized) {
            compositionRoot = (requireActivity() as MainActivity).getCompositionRoot()
        }
        return compositionRoot
    }
}