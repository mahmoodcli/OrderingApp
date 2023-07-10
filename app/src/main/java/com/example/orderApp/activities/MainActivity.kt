package com.example.orderApp.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orderApp.R
import com.example.orderApp.adapter.MainAdapter
import com.example.orderApp.databinding.ActivityMainBinding
import com.example.orderApp.db.RejectedDb
import com.example.orderApp.model.DbModel
import com.example.orderApp.model.OrderItem
import com.example.orderApp.model.OrderShow
import com.example.orderApp.services.BeepForegroundService
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    var status:String?=null
    var arrayList:ArrayList<OrderShow>?=null
    var adapter:MainAdapter?=null
    @SuppressLint("StringFormatInvalid", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        arrayList= ArrayList()
        var firstName = intent.getStringExtra("firstName")
        var lastName = intent.getStringExtra("lastName")
        var email = intent.getStringExtra("email")
        var id = intent.getStringExtra("id")
        var description = intent.getStringExtra("description")
        var orderingMethod = intent.getStringExtra("orderingMethod")
        var paymentMethod = intent.getStringExtra("paymentMethod")
        var phone = intent.getStringExtra("phone")
        status = intent.getStringExtra("status")
        var date = intent.getStringExtra("date")
        var orderIds = intent.getIntExtra("orderIds", 0)
        var menuItemId = intent.getIntExtra("menuItemId", 0)
        var menuItemNam = intent.getStringExtra("menuItemNam")
        var price = intent.getStringExtra("price")
        var quantity = intent.getStringExtra("quantity")
        var totalPrice = intent.getStringExtra("totalPrice")
        var name = intent.getStringExtra("name")
        var total = intent.getStringExtra("total")
        var totalPrices = intent.getStringExtra("totalPrices")
        binding.totalPrices.text="£."+"$totalPrices"
        Log.d("orderName",price.toString())
       /* binding.orderDate.isSelected=true
        binding.orderDate.setHorizontallyScrolling(true)
        binding.orderDate.marqueeRepeatLimit=-1
        binding.menuItemNam.isSelected=true
        binding.menuItemNam.setHorizontallyScrolling(true)
        binding.menuItemNam.marqueeRepeatLimit=-1
        binding.email.isSelected=true
        binding.email.setHorizontallyScrolling(true)
        binding.email.marqueeRepeatLimit=-1
        binding.description.isSelected=true
        binding.description.setHorizontallyScrolling(true)
        binding.description.marqueeRepeatLimit=-1*/
        var input=menuItemNam
        val cleanString=input?.removeSurrounding("[","]")
        val items=cleanString?.split(", ")
        var inputQuantity =quantity
        val cleanStringQuantity=inputQuantity?.removeSurrounding("[","]")
        val itemsQuantity=cleanStringQuantity?.split(", ")
        val cleanPrice=price?.removeSurrounding("[","]")
        val itemPrice=cleanPrice?.split(", ")
//        val ifFloat=itemPrice.toString().toFloatOrNull()
//        val intValue=ifFloat?.toUInt().toString()
        if (items != null && itemsQuantity != null) {
            for (index in items.indices) {
                val item = items[index]
                val quantity = itemsQuantity.getOrNull(index) ?: ""
                val prices = itemPrice?.getOrNull(index) ?: ""
                arrayList?.add(OrderShow(
                    description.toString(), item,
                    prices, quantity, totalPrice.toString()))
            }
        }

        binding.recyclerMain.layoutManager=LinearLayoutManager(this)
        adapter= MainAdapter(arrayList!!,this)
        binding.recyclerMain.adapter=adapter

        if (status=="accepted"){
            binding.linearMain.visibility = View.GONE
            binding.imgPendings.visibility = View.GONE
            binding.recyclerMain.visibility = View.GONE
            binding.cardOrder.visibility = View.VISIBLE
            binding.imgPending.setImageDrawable(resources.getDrawable(R.drawable.accepted))
        }else if (status=="rejected"){
            binding.linearMain.visibility = View.GONE
            binding.imgPendings.visibility = View.GONE
            binding.recyclerMain.visibility = View.GONE
            binding.cardOrder.visibility = View.VISIBLE
            binding.imgPending.setImageDrawable(resources.getDrawable(R.drawable.rejected))
        }else{
            binding.btnAccept.visibility = View.VISIBLE
            binding.btnReject.visibility = View.VISIBLE
        }

        binding.toolMain.setNavigationOnClickListener {
          onBackPressed()
        }
        supportActionBar?.hide()
        acceptButton = findViewById(R.id.btnAccept)
        rejectButton = findViewById(R.id.btnReject)
        acceptButton.setOnClickListener {
            var firstName = intent.getStringExtra("firstName")
            var lastName = intent.getStringExtra("lastName")
            var email = intent.getStringExtra("email")
            var id = intent.getStringExtra("id")
            var description = intent.getStringExtra("description")
            var orderingMethod = intent.getStringExtra("orderingMethod")
            var paymentMethod = intent.getStringExtra("paymentMethod")
            var phone = intent.getStringExtra("phone")
            var status = intent.getStringExtra("status")
            var date = intent.getStringExtra("date")
            var orderIds = intent.getIntExtra("orderIds", 0)
            var menuItemId = intent.getIntExtra("menuItemId", 0)
            var menuItemNam = intent.getStringExtra("menuItemNam")
            var price = intent.getStringExtra("price")
            var quantity = intent.getStringExtra("quantity")
            var totalPrices = intent.getStringExtra("totalPrices")

//            FirebaseMessageReceiver?.mediaPlayer?.stop()
//            FirebaseMessageReceiver.notificationManager?.cancelAll()
            //  printToBluetoothPrinter("Hello, World!")
            var intent = Intent(this, ConfirmOrderActivity::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("email", email)
            intent.putExtra("id", id)
            intent.putExtra("description", description)
            intent.putExtra("orderingMethod", orderingMethod)
            intent.putExtra("paymentMethod", paymentMethod)
            intent.putExtra("phone", phone)
            intent.putExtra("status", status)
            intent.putExtra("date", date)
            intent.putExtra("orderIds", orderIds)
            intent.putExtra("menuItemId", menuItemId)
            intent.putExtra("menuItemNam", menuItemNam)
            intent.putExtra("price", price)
            intent.putExtra("quantity", quantity)
            intent.putExtra("totalPrice", totalPrices)
            startActivity(intent)
            stopService(Intent(this@MainActivity,BeepForegroundService::class.java))
        }

        rejectButton.setOnClickListener {
            Log.d("addtdc", stats.toString())
            var orderIds = intent.getStringExtra("orderIds")
            val orderId = orderIds
            rejectOrder(orderId.toString())
            onBackPressed()
            stopService(Intent(this@MainActivity,BeepForegroundService::class.java))
            var dbModel=DbModel()
            dbModel.title= "Order $id"
            dbModel.desc= date.toString()
            dbModel.email=email.toString()
            dbModel.phone=phone.toString()
            dbModel.status="rejected"
            dbModel.item=menuItemNam.toString()
            dbModel.price=totalPrice.toString()
            Log.d("dsdsds", title.toString())
            var dbHistory=RejectedDb(this)
            dbHistory.saveData(dbModel)
        }

        if (name!=null&&total!=null){
            binding.name.text = name
            binding.totalPrice.text = "$total£"
            binding.oMethod.visibility=View.GONE
            binding.pMethod.visibility=View.GONE
//            binding.oId.visibility=View.GONE
//            binding.mItem.visibility=View.GONE
            binding.pPrice.visibility=View.GONE
            binding.qQuant.visibility=View.GONE
            binding.phoneV.visibility=View.GONE
//            binding.idV.visibility=View.GONE
            binding.menuV.visibility=View.GONE
            binding.orderV.visibility=View.GONE
//            binding.ordersV.visibility=View.GONE
            binding.privev.visibility=View.GONE
        }else{
            binding.name.text = "$firstName $lastName"
            binding.totalPrice.text = "$totalPrice£"
        }
        binding.email.text = email
        if (description.isNullOrEmpty()) {
            binding.description.text = "description is empty"
        } else {
            binding.description.text = description
        }
//        if (id.isNullOrEmpty()) {
//            binding.id.text = "id is empty"
//        } else {
//            binding.id.text = id
//        }
        binding.orderingMethod.text = orderingMethod
        binding.paymentMethod.text = paymentMethod
        binding.phone.text = phone
        binding.status.text = status
        binding.orderDate.text = date

//        binding.orderIds.text = orderIds.toString()
//        binding.menuItemId.text = menuItemId.toString()
        binding.menuItemNam.text = menuItemNam
        binding.price.text = "$price£"
        binding.quantity.text = quantity.toString()

    }

    override fun onBackPressed() {
        if (status=="accepted"||status=="rejected"){
            onBackPressedDispatcher.onBackPressed()
            finish()
        }else{
        startActivity(Intent(this, OrderStatusActivity::class.java))
        finish()
    }
    }

    private fun rejectOrder(orderId: String) {
        var sharedPreferences=getSharedPreferences("MyPrefs",0)
        var userid=sharedPreferences.getString("userId","")
        var jwt=sharedPreferences.getString("token","")
        updateOrderStatus(orderId, userid!!, "rejected", jwt!!)
    }
    inner class WebAppInterface {
        @JavascriptInterface
        fun onLocationReceived(latitude: Double, longitude: Double) {
            // Handle the received location data
            // You can perform any necessary actions with the latitude and longitude values
            // For example, display the current location coordinates in your Kotlin code
            // or calculate the distance to a user location.
        }
    }
companion object {
    lateinit var binding: ActivityMainBinding
    private lateinit var acceptButton: Button
    private lateinit var rejectButton: Button
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    var stats:String?=null

    fun updateOrderStatus(orderId: String, userId: String, status: String, token: String) {
        val baseUrl = "https://api.freeorder.co.uk/api/Order/$orderId"
        val client = OkHttpClient()
        val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
        urlBuilder?.addQueryParameter("userid", userId)
        urlBuilder?.addQueryParameter("status", status)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .put(RequestBody.create(null, ""))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OrderManager", "Failed to update order status. Error: ${e.message}")
                // Handle failure, show error to the user, etc.
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    stats = status
                    Log.d("OrderMfanager", stats!!)
                    // Handle successful response, update UI, etc.
                } else {
                    Log.e("OrderManager", "Failed to update order status. Status: ${response.code}")
                    // Handle API error, show error to the user, etc.
                }
            }
        })
    }
}
}
