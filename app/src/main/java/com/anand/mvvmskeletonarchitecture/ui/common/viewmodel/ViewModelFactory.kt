package com.anand.mvvmskeletonarchitecture.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsViewModel
import com.anand.mvvmskeletonarchitecture.networking.facts.GetFactsUseCase

class ViewModelFactory(private val getFactsUseCase: GetFactsUseCase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: ViewModel
        if (modelClass == FactsViewModel::class.java){
            viewModel = FactsViewModel(getFactsUseCase)
        } else{
            throw RuntimeException("Invalid ViewModel class " + modelClass)
        }
        return viewModel as T
    }
}