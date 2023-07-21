package com.example.orderApp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderApp.R
import com.example.orderApp.activities.MainActivity
import com.example.orderApp.model.Orders
import com.example.orderApp.model.Orderss

class MainAdapterTwo(var courseModelArrayList: ArrayList<Orderss>,var context: Context) : RecyclerView.Adapter<MainAdapterTwo.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // below line is to inflate our layout.
        val view: View = LayoutInflater.from(context).inflate(R.layout.dashboard_items, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = courseModelArrayList[position]
        val item = model.items[position%model.items.size]
        Log.e("OrderManxagerxdss", item.totalPrice.toString())

        holder.courseNameTV.setText(item.menuItemName.toString())
        holder.txtQuantity.setText(item.quantity.toString())
        holder.txtPrice.setText("£"+item.price.toString())
        holder.btnDelete.visibility=View.GONE
        holder.courseDescTV.visibility=View.GONE
    }

    override fun getItemCount(): Int {
        // returning the size of array list.
        return courseModelArrayList.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views.
        lateinit var courseNameTV: TextView
        lateinit var courseDescTV: TextView
        lateinit var txtPrice: TextView
        lateinit var txtQuantity: TextView
        lateinit var card_item: CardView
        lateinit var btnDelete: ImageView

        init {
            // initializing our views with their ids.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName)
            courseDescTV = itemView.findViewById(R.id.idTVCourseDescription)
            card_item = itemView.findViewById(R.id.card_item)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            txtQuantity = itemView.findViewById(R.id.txtQuantity)
        }
    }

    // creating a constructor for our variables.
}
