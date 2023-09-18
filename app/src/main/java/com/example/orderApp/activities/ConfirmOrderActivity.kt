package com.example.orderApp.activities

import android.app.ProgressDialog
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
import com.example.orderApp.fragment.DashboardActivity
import com.example.orderApp.model.DbModel
import com.example.orderApp.model.OrderShow
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.io.ByteArrayOutputStream
import java.io.IOException

class ConfirmOrderActivity : AppCompatActivity() {
    lateinit var binding:ActivityConfirmOrderBinding
    var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConfirmOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        var sharedPreferences =getSharedPreferences("MyPrefs", 0)
        val userId = sharedPreferences!!.getString("userId", "")
        val jwt = sharedPreferences!!.getString("token", "")
        companyDetail(userId!!, jwt!!)
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
        Log.d("orderName", firstName.toString())
        binding.orderNumber.text="Order: $id"


        binding.name.text="Name: $firstName $lastName"
        binding.email.text= "Email: $email"
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
        val dated= listOf('T','.')
        val delimiter = '.'
        var resultString = date?.substringBefore(delimiter)
        for (char in dated) {
        resultString=resultString?.replace(char.toString(), ", ")
        }
        binding.orderDate.text="Dated : $resultString"

//        binding.orderIds.text= orderIds.toString()
//        binding.menuItemId.text= menuItemId.toString()
        var input = menuItemNam
        val cleanString = input?.removeSurrounding("[", "]")
        val items = cleanString?.split(", ")

        var quant = quantity?.removeSurrounding("[", "]")
        val itemQuant = quant?.split(", ")

        var pric = price?.removeSurrounding("[", "]")
        val itemPrice = pric?.split(", ")

        if (items != null && itemQuant != null && itemPrice != null) {
            // Join the menuItemNam values with newline separator
            val menuItemText = items.joinToString("\n")
            val quantityText = itemQuant.joinToString("\n")
            val priceText = itemPrice.joinToString("\n")

            // Set the text of your TextViews
            binding.menuItemNam.text = menuItemText
            binding.quantity.text = quantityText
            binding.price.text = priceText
        }

        binding.totalPrice.text= "£ $totalPrice"


        binding.acceptConfirm.setOnClickListener {
            var firstName=intent.getStringExtra("firstName")
            var lastName=intent.getStringExtra("lastName")

            var sharedPreferences=getSharedPreferences("myOrders",Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("orders","$firstName $lastName").apply()

            val map = convertScrollViewToBitmap(binding.scrollView)

            val stream = ByteArrayOutputStream()
            map?.compress(Bitmap.CompressFormat.PNG,  100, stream)
            val byteArray: ByteArray = stream.toByteArray()

            val in1 = Intent(this, AcceptOrderActivity::class.java)
            in1.putExtra("image", byteArray)
            startActivity(in1)
            Log.v("BitmapObject", map.toString())
            var orderIds=intent.getStringExtra("id")
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
            MainActivity.updateOrderStatus(orderId, userid!!, "Accepted", jwt!!)
            Log.d("addtdc", MainActivity.stats.toString())
    }
    fun companyDetail(userId:String,jwt:String){
        pd = ProgressDialog(this)
        pd!!.setMessage("Loading!...")
        pd?.setCanceledOnTouchOutside(false)
        pd!!.show()
        val url="https://api.freeorder.co.uk/api/Company/getcompanybyuserid/$userId"
        val client=OkHttpClient()
        val request=Request.Builder().url(url).header("Authorization","Bearer $jwt").get().build()

        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@ConfirmOrderActivity,"failed to load",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    val detail = JSONObject(responseData)
                    pd?.dismiss()
                    val id = detail.optInt("id")
                    val email = detail.optString("email", "")
                    val address1 = detail.optString("address1", "")
                    val address2 = detail.optString("address2", "")
                    val city = detail.optString("city", "")
                    val postCode = detail.optString("postCode", "")
                    val country = detail.optString("country", "")
                    val isActive = detail.optBoolean("isActive")
                    val isDeleted = detail.optBoolean("isDeleted")
                    val restaurantName = detail.optString("restaurantName", "")
                    val restaurantPhoneNumber = detail.optString("restaurantPhoneNumber", "")
                    val allow = detail.optBoolean("allow")
                    val url = detail.optString("url", "")
                    val firstName = detail.optString("firstName", "")
                    val lastName = detail.optString("lastName", "")
                    runOnUiThread {
                        binding.restaurantName.text=restaurantName
                        binding.address.text=address1
                        binding.city.text=city
                    }

                }
            }
        })
    }
}