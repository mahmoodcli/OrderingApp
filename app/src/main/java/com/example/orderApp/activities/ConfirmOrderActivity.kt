package com.example.orderApp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderApp.databinding.ActivityConfirmOrderBinding
import com.example.orderApp.db.DbHistory
import com.example.orderApp.model.DbModel
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class ConfirmOrderActivity : AppCompatActivity() {
    lateinit var binding:ActivityConfirmOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConfirmOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toolMain.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        var firstName=intent.getStringExtra("firstName")
        var lastName=intent.getStringExtra("lastName")
        val email=intent.getStringExtra("email")
        var id=intent.getStringExtra("id")
        var description=intent.getStringExtra("description")
        var orderingMethod=intent.getStringExtra("orderingMethod")
        var paymentMethod=intent.getStringExtra("paymentMethod")
        var phone=intent.getStringExtra("phone")
        var status=intent.getStringExtra("status")
        var date=intent.getStringExtra("date")
        var orderIds=intent.getIntExtra("orderIds",0)
        var menuItemId=intent.getIntExtra("menuItemId",0)
        var menuItemNam=intent.getStringExtra("menuItemNam")
        var price=intent.getStringExtra("price")
        var quantity=intent.getStringExtra("quantity")
        var totalPrice=intent.getStringExtra("totalPrice")

        binding.name.text="$firstName $lastName"
        binding.email.text= email
        if (description.isNullOrEmpty()){
            binding.description.text="description is empty"
        }else{
            binding.description.text=description
        }
       /* if (id.isNullOrEmpty()){
            binding.id.text="id is empty"
        }else{
            binding.id.text=id
        }*/
        binding.orderingMethod.text=orderingMethod
        binding.paymentMethod.text=paymentMethod
        binding.phone.text=phone
        binding.status.text=status
        binding.orderDate.text=date

//        binding.orderIds.text= orderIds.toString()
//        binding.menuItemId.text= menuItemId.toString()
        binding.menuItemNam.text=menuItemNam
        binding.price.text= price.toString()
        binding.quantity.text= quantity.toString()
        binding.totalPrice.text= totalPrice.toString()

        binding.acceptConfirm.setOnClickListener {
            var firstName=intent.getStringExtra("firstName")
            var lastName=intent.getStringExtra("lastName")

            var dbModel=DbModel()
            dbModel.title="Order $id"
            dbModel.desc= date.toString()
            dbModel.email=email.toString()
            dbModel.phone=phone.toString()
            dbModel.status="accepted"
            dbModel.item=menuItemNam.toString()
            dbModel.price=totalPrice.toString()
            Log.d("dsdsds", title.toString())
            var dbHistory=DbHistory(this)
            dbHistory.saveData(dbModel)

            var sharedPreferences=getSharedPreferences("myOrders",Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("orders","$firstName $lastName").apply()

            val map = convertScrollViewToBitmap(binding.scrollView)
            val stream = ByteArrayOutputStream()
            map?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            val in1 = Intent(this, AcceptOrderActivity::class.java)
            in1.putExtra("image", byteArray)
            startActivity(in1)
            Log.v("BitmapObject", map.toString())
            var orderIds=intent.getStringExtra("orderIds")
            val orderId =orderIds
            acceptOrder(orderId.toString())
            finish()
        }
    }

    fun convertScrollViewToBitmap(scrollView: ScrollView): Bitmap {
        var totalHeight = 0


        for (i in 0 until scrollView.childCount) {
            totalHeight += scrollView.getChildAt(i).height
        }

        scrollView.measure(
            View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(totalHeight, View.MeasureSpec.EXACTLY)
        )
        scrollView.layout(0, 0, scrollView.measuredWidth, scrollView.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            scrollView.measuredWidth,
            scrollView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Set white background color
        scrollView.draw(canvas)
        return bitmap
    }

    private fun acceptOrder(orderId: String) {
            var sharedPreferences=getSharedPreferences("MyPrefs",0)
            var userid=sharedPreferences.getString("userId","")
            var jwt=sharedPreferences.getString("token","")
            MainActivity.updateOrderStatus(orderId, userid!!, "accepted", jwt!!)
            Log.d("addtdc", MainActivity.stats.toString())
    }
}