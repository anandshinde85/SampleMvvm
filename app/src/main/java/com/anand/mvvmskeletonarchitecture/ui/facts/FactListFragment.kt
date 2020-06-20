package com.anand.mvvmskeletonarchitecture.ui.facts

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anand.mvvmskeletonarchitecture.R
import com.anand.mvvmskeletonarchitecture.common.util.Resource
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsResponse
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsViewModel
import com.anand.mvvmskeletonarchitecture.networking.facts.Rows
import com.anand.mvvmskeletonarchitecture.repository.Failure
import com.anand.mvvmskeletonarchitecture.ui.common.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_fact_list.*

class FactListFragment : BaseFragment() {
    private lateinit var factsViewModel: FactsViewModel
    private lateinit var factsAdapter: FactListAdapter

    override fun getLayoutResourceId() = R.layout.fragment_fact_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInjector().inject(this)
        factsViewModel = ViewModelProvider(this, viewModelFactory).get(FactsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    private fun initUi() {
        factsAdapter =
            FactListAdapter(mutableListOf(), onFactClicked = { fact ->
                onFactClicked(fact)
            })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = factsAdapter
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            factsViewModel.loadData()
        }
    }

    private fun initObserver() {
        // There are 2 ways to observe
        // 1. Using live data
//        factsViewModel.liveData.observe(viewLifecycleOwner, Observer {
//            handleFactListResponse(it)
//        })

        // 2. Using single live data
        factsViewModel.singleLiveData.takeUnless { it.hasActiveObservers() }
            ?.observe(viewLifecycleOwner, Observer {
                handleFactListResponse(it)
            })
        factsViewModel.loadData()
    }

    private fun handleFactListResponse(resourceState: Resource<FactsResponse?>?) {
        when (resourceState) {
            is Resource.Loading -> {
                Toast.makeText(requireContext(), "Loading", Toast.LENGTH_LONG).show()
                showLoading()
            }
            is Resource.LoadedWithApiInProgress -> {
                Toast.makeText(requireContext(), "LoadedWithApiInProgress", Toast.LENGTH_LONG)
                    .show()
                showFactList(resourceState.data)
            }
            is Resource.Loaded -> {
                Toast.makeText(requireContext(), "Loaded", Toast.LENGTH_LONG).show()
                showFactList(resourceState.data)
            }
            is Resource.FailedWithCacheData -> {
                Toast.makeText(requireContext(), "FailedWithCacheData", Toast.LENGTH_LONG).show()
                showFailed(resourceState.exception)
            }
            is Resource.Failed -> {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
                showFailed(resourceState.exception)
            }
        }
    }

    private fun showFailed(exception: Failure) {
        progressBar.visibility = GONE
        tvEmpty.visibility = VISIBLE
        when (exception) {
            is Failure.NetworkConnection -> {
                tvEmpty.text = getString(R.string.no_network_msg)
            }
            else -> {
                progressBar.visibility = GONE
                tvEmpty.text = getString(R.string.error_msg)
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = VISIBLE
        recyclerView.visibility = GONE
        tvEmpty.visibility = GONE
    }

    private fun hideLoading() {
        progressBar.visibility = GONE
        recyclerView.visibility = VISIBLE
        tvEmpty.visibility = GONE
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun showFactList(factsResponse: FactsResponse?) {
        factsResponse?.let {
            hideLoading()
            requireActivity().title = it.title
            factsAdapter.updateList(it.rows)
        }
    }

    private fun onFactClicked(rows: Rows) {
//        Navigation.findNavController(recyclerView)
//            .navigate(FactsListFragmentDirections.factsListFragmentToFactDetailsFragment(rows))
    }
}