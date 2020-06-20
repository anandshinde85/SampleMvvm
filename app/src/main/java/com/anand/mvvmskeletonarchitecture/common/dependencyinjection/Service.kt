package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation class to inject dependencies
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR)
annotation class Service