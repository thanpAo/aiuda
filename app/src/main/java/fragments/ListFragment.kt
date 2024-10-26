package com.example.aiuda.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aiuda.Item
import com.example.aiuda.ItemAdapter
import com.example.aiuda.R

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items = listOf(
            Item("Item 1", "SKU001", R.drawable.image1),
            Item("Item 2", "SKU002", R.drawable.image2),
            Item("Item 3", "SKU003", R.drawable.image3)
        )

        val adapter = ItemAdapter(requireContext(), items)
        recyclerView.adapter = adapter

        return view
    }
}
