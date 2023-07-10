package com.example.orderApp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderApp.R
import com.example.orderApp.adapter.AcceptedAdapter
import com.example.orderApp.db.DbHistory
import com.example.orderApp.db.RejectedDb
import com.example.orderApp.model.DbModel

class RejectedFragment: Fragment() {
    var recyclerView:RecyclerView?=null
    var filter:String =""
    var noData: TextView?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v=inflater.inflate(R.layout.rejected_fragment,container,false)
        recyclerView=v.findViewById(R.id.recyclerReject)
        noData=v.findViewById(R.id.txtNoDatare)
        dataList(filter)
        return v
    }

    fun dataList(filter:String){
        var sqLiteOpenHelper= RejectedDb(requireActivity())
        var adapter= AcceptedAdapter(sqLiteOpenHelper.dataList(filter) as MutableList<DbModel>,requireActivity(), recyclerView!!)
        recyclerView?.layoutManager= LinearLayoutManager(requireActivity())
        recyclerView?.adapter=adapter
        if (adapter.itemCount.equals(0)){
            noData?.visibility=View.VISIBLE
        }else{
            noData?.visibility=View.GONE
        }

    }
}