package com.anand.mvvmskeletonarchitecture

import android.os.Bundle
import com.anand.mvvmskeletonarchitecture.ui.common.activities.BaseActivity
import com.anand.mvvmskeletonarchitecture.ui.facts.FactListFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        getCompositionRoot()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, FactListFragment())
                    .commitNow()
        }
    }
}