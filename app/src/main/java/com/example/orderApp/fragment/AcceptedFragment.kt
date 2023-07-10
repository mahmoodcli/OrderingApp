package com.example.orderApp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderApp.R
import com.example.orderApp.adapter.AcceptedAdapter
import com.example.orderApp.adapter.DashboardAdapter
import com.example.orderApp.db.DbHistory
import com.example.orderApp.model.DbModel
import com.example.orderApp.model.Orders
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class AcceptedFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var noData:TextView?=null
    var filter:String =""
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v=inflater.inflate(R.layout.accept_fragment,container,false)
        recyclerView=v.findViewById(R.id.recyclerAccept)
        noData=v.findViewById(R.id.txtNoData)
        dataList(filter)
        return v
    }

    fun dataList(filter:String){
        var sqLiteOpenHelper=DbHistory(requireActivity())
        var adapter=AcceptedAdapter(sqLiteOpenHelper.dataList(filter) as MutableList<DbModel>,requireActivity(), recyclerView!!)
        recyclerView?.layoutManager=LinearLayoutManager(requireActivity())
        recyclerView?.adapter=adapter
        if (adapter.itemCount.equals(0)){
            noData?.visibility=View.VISIBLE
        }else{
            noData?.visibility=View.GONE
        }
    }
}