package com.burakiren.socketapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakiren.socketapp.R
import com.burakiren.socketapp.model.MockData
import kotlinx.android.synthetic.main.adapter_datas.view.*


class MockDataAdapter(private var mockDatas: List<MockData>) :
    RecyclerView.Adapter<MockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_datas, parent, false)
        return MockViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mockDatas.size
    }

    override fun onBindViewHolder(holder: MockViewHolder, position: Int) {
        val post = mockDatas[position]
        holder.bindView(post)
        holder.itemView.setOnClickListener {
        }
    }

    fun update(datas:List<MockData>){
        mockDatas= datas
        notifyDataSetChanged()
    }
}

class MockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(data: MockData) {
        itemView.apply {
            dataTitle.text = data.name

        }
    }
}
