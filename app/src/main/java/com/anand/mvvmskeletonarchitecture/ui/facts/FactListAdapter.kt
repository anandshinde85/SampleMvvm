package com.anand.mvvmskeletonarchitecture.ui.facts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.anand.mvvmskeletonarchitecture.R
import com.anand.mvvmskeletonarchitecture.databinding.FactListItemBinding
import com.anand.mvvmskeletonarchitecture.networking.facts.Rows

class FactListAdapter(
    private val factList: MutableList<Rows>,
    val onFactClicked: (Rows) -> Unit
) : RecyclerView.Adapter<FactsViewHolder>(), FactClickListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<FactListItemBinding>(
            inflater,
            R.layout.fact_list_item,
            parent,
            false
        )
        return FactsViewHolder(view)
    }

    override fun getItemCount() = factList.size

    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val fact = factList[position]

        holder.view.row = fact
        holder.itemView.setOnClickListener {
            onFactClicked(fact)
        }
    }

    fun updateList(facts: List<Rows>) = factList.apply {
        clear()
        addAll(facts)
        notifyDataSetChanged()
    }

    override fun onFactClicked(view: View) {
//        onFactClicked(fact)
    }
}

class FactsViewHolder(var view: FactListItemBinding) : RecyclerView.ViewHolder(view.root)

interface FactClickListener {
    fun onFactClicked(view: View)
}