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

class DashboardAdapter(courseModelArrayList: ArrayList<Orders>,var context: Context) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    // creating a variable for array list and context.
    private var courseModelArrayList: ArrayList<Orders>

    // method for filtering our recyclerview items.
    fun filterList(filterList: ArrayList<Orders>) {
        // below line is to add our filtered
        // list in our course array list.
        courseModelArrayList = filterList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // below line is to inflate our layout.
        val view: View = LayoutInflater.from(context).inflate(R.layout.dashboard_items, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to our views of recycler view.
        val model = courseModelArrayList[position]
        val item=model.items[position%model.items.size]
        val dated= listOf('T','.')
        val delimiter = '.'
        var resultString = model.orderDate?.substringBefore(delimiter)
        for (char in dated) {
            resultString=resultString?.replace(char.toString(), ", ")
        }
        holder.courseDescTV.text="$resultString"
            holder.courseNameTV.setText("Order "+model.id)
            holder.txtQuantity.setText(model.orderLength.toString())
            holder.name.setText(model.firstName+" "+model.lastName)
        holder.name.visibility=View.VISIBLE

        if (model.orderingMethod=="delivery"){
            holder.postcode.setText(model.postcode)
            holder.postcode.visibility=View.VISIBLE
        }else{
            holder.postcode.visibility=View.GONE
        }

        val priceClean = item.totalPrice.toString().removeSurrounding("[", "]")
        val convert = priceClean.split(", ")

        val totalPrice: String = if (convert.size > 1) {
            val sum = convert.map { it.toFloatOrNull() ?: 0.0f }.sum()
            sum.toString()
        } else {
            convert.firstOrNull() ?: ""
        }

        holder.txtPrice.text = "£$totalPrice"




        Log.e("OrderManasdsssger", item.price.toString()+" "+item.totalPrice)

        holder.courseDescTV.setSelected(true);
        holder.courseDescTV.setHorizontallyScrolling(true);
        holder.courseDescTV.setMarqueeRepeatLimit(-1);
        Log.e("OrderManassger", model.firstName)
        holder.card_item.setOnClickListener {
            var intent=Intent(context, MainActivity::class.java)
            intent.putExtra("firstName",model.firstName)
            intent.putExtra("lastName",model.lastName)
            intent.putExtra("email",model.email)
            intent.putExtra("id",model.id)
            intent.putExtra("description",model.description)
            intent.putExtra("orderingMethod",model.orderingMethod)
            intent.putExtra("paymentMethod",model.paymentMethod)
            intent.putExtra("phone",model.phone)
            intent.putExtra("status",model.status)
            intent.putExtra("date",model.orderDate)
            intent.putExtra("address",model.address1)
            intent.putExtra("city",model.city)
            intent.putExtra("postcode",model.postcode)

            intent.putExtra("orderIds",model.id)
            intent.putExtra("menuItemId",item.menuItemId)
            intent.putExtra("menuItemNam",item.menuItemName.toString())
            intent.putExtra("price",item.price.toString())
            intent.putExtra("quantity",item.quantity.toString())
            intent.putExtra("totalPrice",item.totalPrice.toString())
            intent.putExtra("totalPrices",totalPrice)
            context.startActivity(intent)
        }
        holder.btnDelete.visibility=View.GONE
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
        lateinit var name: TextView
        lateinit var postcode: TextView

        init {
            // initializing our views with their ids.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName)
            courseDescTV = itemView.findViewById(R.id.idTVCourseDescription)
            card_item = itemView.findViewById(R.id.card_item)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            txtQuantity = itemView.findViewById(R.id.txtQuantity)
            name = itemView.findViewById(R.id.name)
            postcode = itemView.findViewById(R.id.postcode)
        }
    }

    // creating a constructor for our variables.
    init {
        this.courseModelArrayList = courseModelArrayList
    }
    fun updateData(newData:ArrayList<Orders>) {
        courseModelArrayList = newData
        notifyDataSetChanged()
    }
}
