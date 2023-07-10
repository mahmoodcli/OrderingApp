package com.example.orderApp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderApp.R
import com.example.orderApp.activities.MainActivity
import com.example.orderApp.db.DbHistory
import com.example.orderApp.db.RejectedDb
import com.example.orderApp.model.DbModel

class AcceptedAdapter(var list :MutableList<DbModel>,var context: Context,var recyclerView: RecyclerView): RecyclerView.Adapter<AcceptedAdapter.ViewHold>() {
    class ViewHold(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var courseNameTV: TextView
        lateinit var courseDescTV: TextView
        lateinit var txtPrice: TextView
        lateinit var card_item: CardView
        lateinit var card_2: CardView
        lateinit var deleteItem: ImageView
        init {
            courseNameTV = itemView.findViewById(R.id.idTVCourseName)
            courseDescTV = itemView.findViewById(R.id.idTVCourseDescription)
            card_item = itemView.findViewById(R.id.card_item)
            deleteItem = itemView.findViewById(R.id.btnDelete)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            card_2 = itemView.findViewById(R.id.card_2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        var v=LayoutInflater.from(context).inflate(R.layout.dashboard_items,parent,false)
        return ViewHold(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHold, position: Int) {
       var data=list.get(position)
        holder.courseNameTV.text=data.title
        holder.courseDescTV.text=data.desc
        holder.card_2.visibility=View.GONE
        holder.txtPrice.visibility=View.GONE
        holder.courseDescTV.setSelected(true);
        holder.courseDescTV.setHorizontallyScrolling(true);
        holder.courseDescTV.setMarqueeRepeatLimit(-1);
        holder.card_item.setOnClickListener {
            var intent= Intent(context, MainActivity::class.java)
            intent.putExtra("date",data.desc)
            intent.putExtra("name",data.title)
            intent.putExtra("menuItemNam",data.item)
            intent.putExtra("total",data.price)
            intent.putExtra("phone",data.phone)
            intent.putExtra("email",data.email)
            intent.putExtra("status",data.status)
            context.startActivity(intent)
        }
        holder.deleteItem.setOnClickListener {
            var alertDialog= AlertDialog.Builder(context)
                .setTitle("Are you sure?")
                .setMessage("Delete accept and reject order history!")
                .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (data.status=="rejected") {
                        var db2 = RejectedDb(context)
                        db2.deleteData(data.id,context)
                    }else{
                        var db1= DbHistory(context)
                        db1.deleteData(data.id,context)
                    }
                    list.removeAt(position)
                    recyclerView.removeViewAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,list.size)
                    notifyDataSetChanged()
                    Toast.makeText(context,"Delete Successfully!", Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("cancel",null)
            alertDialog.show()
        }
    }
}