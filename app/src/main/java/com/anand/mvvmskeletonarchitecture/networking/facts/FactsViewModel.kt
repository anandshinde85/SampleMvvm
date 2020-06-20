package com.anand.mvvmskeletonarchitecture.networking.facts

import com.anand.mvvmskeletonarchitecture.common.viewmodels.BaseViewModel

//class FactsViewModel @Inject constructor(var interactionsUseCase: GetInteractionsUseCase) :
class FactsViewModel(var factsUseCase: GetFactsUseCase) :
    BaseViewModel<Unit, FactsResponse>(factsUseCase) {

    override fun load(query: Unit, forceRefresh: Boolean) {
        super.load(query, forceRefresh)
        factsUseCase(query, forceRefresh)
    }

    public override fun onCleared() {
        factsUseCase.cancel()
        super.onCleared()
    }
}