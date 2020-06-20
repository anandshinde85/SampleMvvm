package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class Named(
    /** The name.  */
    val value: String = ""
)