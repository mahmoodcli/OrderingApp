package com.example.orderApp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orderApp.R
import com.example.orderApp.adapter.MainAdapter
import com.example.orderApp.adapter.MainAdapterTwo
import com.example.orderApp.databinding.ActivityMainBinding
import com.example.orderApp.model.*
import com.example.orderApp.services.BeepForegroundService
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.converter.ArabicConverter
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    var status:String?=null
    var arrayList:ArrayList<OrderShow>?=null
    var adapter:MainAdapter?=null
    private var printing : Printing? = null
    private var courseModelArrayList: ArrayList<Orderss>? = null
    var sharedPreferences:SharedPreferences?=null
    var editor:Editor?=null
    var handler:Handler?=null
    var pd:ProgressDialog?=null
    var runnable:Runnable?=null
    @SuppressLint("StringFormatInvalid", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Printooth.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("myAllData",0)
        var firstNamse = sharedPreferences?.getString("firstName", "")
        Log.d("orderName", firstNamse.toString())
        pd= ProgressDialog(this)
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initViews()
        initListeners()
        editor=sharedPreferences?.edit()
        handler= Handler()
        runnable= Runnable {
            Log.d("addtdc", stats.toString())
            var orderIds = intent.getStringExtra("orderIds")
            val orderId = orderIds
            rejectOrder(orderId.toString())
            onBackPressed()
            stopService(Intent(this@MainActivity,BeepForegroundService::class.java))
        }
        handler?.postDelayed(runnable!!,3 * 60 * 1000)

        arrayList= ArrayList()
        courseModelArrayList=ArrayList()
        acceptButton = findViewById(R.id.btnAccept)
        rejectButton = findViewById(R.id.btnReject)

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
        var address = intent.getStringExtra("address")
        var city = intent.getStringExtra("city")
        var postcode = intent.getStringExtra("postcode")
        binding.totalPrices.text="£"+"$totalPrices"
        Log.d("orderName",price.toString())

 /*       binding.showLoc.visibility=View.GONE

        if (address.equals("null") && city.equals("null") && postcode.equals("null")) else{
            binding.showLoc.visibility=View.VISIBLE
            binding.showLoc.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),121)
                }
                val query = "$address, $postcode $city"
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                binding.webView.settings.javaScriptEnabled=true
                binding.webView.settings.domStorageEnabled = true
                binding.webView.settings.setGeolocationEnabled(true)

                binding.webView.setWebChromeClient(object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String,
                        callback: GeolocationPermissions.Callback
                    ) {
                        callback.invoke(origin, true, false)
                    }
                })

                binding.webView.visibility=View.VISIBLE
                binding.showLoc.visibility=View.GONE
                binding.btnClose.visibility=View.VISIBLE
                binding.webView.loadUrl("https://www.google.com/maps/search/?api=1&query=$encodedQuery")
            }
            binding.btnClose.setOnClickListener {
                binding.webView.visibility=View.GONE
                binding.showLoc.visibility=View.VISIBLE
                binding.btnClose.visibility=View.GONE
            }
        }*/
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

// Iterating over the ArrayList

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
        if (intent.extras?.containsKey("notification") == true) {
           //binding.showLoc.visibility=View.GONE
            var sharedPreferences = getSharedPreferences("myorder", 0)
            var sharedPreferencess = getSharedPreferences("MyPrefs", 0)
            var userId = sharedPreferencess!!.getString("userId", "")
            var jwt = sharedPreferencess!!.getString("token", "")
            var orderid = sharedPreferences.getString("orderId", "")
            Log.d("sddkjskjd", orderid.toString())
            getOrders(userId!!, jwt!!)

            acceptButton?.setOnClickListener {
                handler?.removeCallbacks(runnable!!)
              val  sharedPreferences=getSharedPreferences("myAllData",0)

                var firstName = sharedPreferences.getString("firstName", "")
                var lastName = sharedPreferences.getString("lastName", "")
                var email = sharedPreferences.getString("email", "")
                var id = sharedPreferences.getString("id", "")

                var description = sharedPreferences.getString("description", "")
                var orderingMethod = sharedPreferences.getString("orderingMethod", "")
                var paymentMethod = sharedPreferences.getString("paymentMethod", "")
                var phone = sharedPreferences.getString("phone", "")
                var status = sharedPreferences.getString("status", "")
                var date = sharedPreferences.getString("orderDate", "")

                var menuItemId = sharedPreferences.getString("menuItemId", "")
                var menuItemNam = sharedPreferences.getString("menuItemName", "")
                var price = sharedPreferences.getString("price", "")
                var quantity = sharedPreferences.getString("quantity", "")
                var totalPrices = sharedPreferences.getString("totalPrice", "")
                var citys = sharedPreferences.getString("city", "null")
                var postcodes = sharedPreferences.getString("postcode", "null")
                var addresss = sharedPreferences.getString("address", "null")
                Log.d("orderName", city+postcode+address)

//            FirebaseMessageReceiver?.mediaPlayer?.stop()
//            FirebaseMessageReceiver.notificationManager?.cancelAll()
                //  printToBluetoothPrinter("Hello, World!")
                var intent = Intent(this, ConfirmOrderActivity::class.java)
                intent.putExtra("firstName", firstName.toString())
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
            rejectButton?.setOnClickListener {
                val  sharedPreferences=getSharedPreferences("myAllData",0)
                var orderIds = sharedPreferences.getString("id", "")
                Log.d("addtdc", stats.toString())
                val orderId = orderIds
                rejectOrder(orderId.toString())
                onBackPressed()
                stopService(Intent(this@MainActivity,BeepForegroundService::class.java))
            }
        }else{
            if (items != null && itemsQuantity != null) {
                for (index in items.indices) {
                    val item = items[index]
                    val quantity = itemsQuantity.getOrNull(index) ?: ""
                    val prices = itemPrice?.getOrNull(index) ?: ""
                    arrayList?.add(OrderShow(description.toString(), item, prices, quantity, totalPrice.toString()))
                }
            }

            binding.recyclerMain.layoutManager=LinearLayoutManager(this)
            adapter= MainAdapter(arrayList!!,this)
            binding.recyclerMain.adapter=adapter
            rejectButton?.setOnClickListener {
                Log.d("addtdc", stats.toString())
                var orderIds = intent.getStringExtra("orderIds")
                val orderId = orderIds
                rejectOrder(orderId.toString())
                onBackPressed()
                stopService(Intent(this@MainActivity,BeepForegroundService::class.java))
            }
            acceptButton?.setOnClickListener {
                handler?.removeCallbacks(runnable!!)
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

        }


        if (status=="accepted"){
            binding.linearMain.visibility = View.GONE
            binding.imgPendings.visibility = View.GONE
            binding.recyclerMain.visibility = View.GONE
            binding.cardOrder.visibility = View.VISIBLE
            binding.printStatus.visibility=View.VISIBLE
            binding.imgPending.setImageDrawable(resources.getDrawable(R.drawable.accepted))

        }else if (status=="rejected"){
            binding.linearMain.visibility = View.GONE
            binding.imgPendings.visibility = View.GONE
            binding.recyclerMain.visibility = View.GONE
            binding.cardOrder.visibility = View.VISIBLE
            binding.printStatus.visibility=View.VISIBLE
            binding.imgPending.setImageDrawable(resources.getDrawable(R.drawable.rejected))
        }else if (status=="notification"){
            binding.imgPending.visibility=View.GONE
        }else{
            binding.btnAccept.visibility = View.VISIBLE
            binding.btnReject.visibility = View.VISIBLE
        }

        binding.shareOrder.setOnClickListener {
            val map = convertScrollViewToBitmap(binding.scrollMain)
            shareBitmap(this,map,"order")
        }

        binding.toolMain.setNavigationOnClickListener {
          onBackPressed()
        }
        supportActionBar?.hide()


        if (name!=null&&total!=null){
            binding.name.text = name
            binding.totalPrice.text = "$total£"
            binding.oMethod.visibility=View.GONE
            binding.pMethod.visibility=View.GONE
//            binding.oId.visibility=View.GONE
//            binding.mItem.visibility=View.GONE
//            binding.pPrice.visibility=View.GONE
//            binding.qQuant.visibility=View.GONE
            binding.phoneV.visibility=View.GONE
//            binding.idV.visibility=View.GONE
            binding.menuV.visibility=View.GONE
            binding.orderV.visibility=View.GONE
//            binding.ordersV.visibility=View.GONE
//            binding.privev.visibility=View.GONE
        }else{
            binding.name.text = "$firstName $lastName"
            val priceClean = totalPrice.toString().removeSurrounding("[", "]")
            val convert = priceClean.split(", ")

            val totalPrice: String = if (convert.size > 1) {
                val sum = convert.map { it.toFloatOrNull() ?: 0.0f }.sum()
                sum.toString()
            } else {
                convert.firstOrNull() ?: ""
            }
            binding.totalPrice.text = totalPrice
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

        if (items != null && itemsQuantity != null && itemPrice != null) {
            // Join the menuItemNam values with newline separator
            val menuItemText = items.joinToString("\n")
            val quantityText = itemsQuantity.joinToString("\n")
            val priceText = itemPrice.joinToString("\n")

            // Set the text of your TextViews
            binding.menuItemNam.text = menuItemText
            binding.quantity.text = quantityText
            binding.price.text = priceText
        }
    }
    fun shareBitmap(context: Context, bitmap: Bitmap, title: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "image.png")

            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            val fileUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (e: Exception) {
            e.printStackTrace()
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
    private fun initViews() {
        var text = if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
        //  Toast.makeText(this@AcceptOrderActivity, text, Toast.LENGTH_SHORT).show()
    }
    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }
    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(
            TextPrintable.Builder()
            .setText(" Hello World : été è à '€' içi Bò Xào Coi Xanh")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        add(
            TextPrintable.Builder()
            .setText("Hello World : été è à €")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        add(
            TextPrintable.Builder()
            .setText("Hello World")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        add(
            TextPrintable.Builder()
            .setText("Hello World")
            .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        add(
            TextPrintable.Builder()
            .setText("اختبار العربية")
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setCharacterCode(DefaultPrinter.CHARCODE_ARABIC_FARISI)
            .setNewLinesAfter(1)
            .setCustomConverter(ArabicConverter()) // change only the converter for this one.
            .build())
    }

    private fun initListeners() {
        /* binding.printOrder.setOnClickListener {
             if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                 ScanningActivity::class.java),
                 ScanningActivity.SCANNING_FOR_PRINTER)
             else printSomePrintable()
         }*/
        binding.printOrder.setOnClickListener {
            val map = convertScrollViewToBitmap(binding.scrollMain)
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            else printSomePrintable()
        }

        /*  btnPiarUnpair.setOnClickListener {
              if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
              else startActivityForResult(Intent(this, ScanningActivity::class.java),
                  ScanningActivity.SCANNING_FOR_PRINTER)
              initViews()
          }

          btnCustomPrinter.setOnClickListener {
              startActivity(Intent(this, WoosimActivity::class.java))
          }*/

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                pd?.setMessage("Printing...")
                pd?.setCanceledOnTouchOutside(false)
                pd?.show()
//                Toast.makeText(this@AcceptOrderActivity, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                pd?.dismiss()
                Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                pd?.dismiss()
                Toast.makeText(this@MainActivity, "Failed to connect with printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                pd?.dismiss()
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                pd?.dismiss()
                Toast.makeText(this@MainActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                pd?.dismiss()
                //        Toast.makeText(this@AcceptOrderActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun printSomeImages(bitmap: Bitmap) {
        val receiptWidthInPixels = (80f * 203 / 25.4f).toInt() // Calculate the receipt width in pixels based on 80mm width and 203 DPI
        val centeredBitmap = centerImageHorizontally(bitmap, receiptWidthInPixels)
        val printables = ArrayList<Printable>().apply {
            add(ImagePrintable.Builder(centeredBitmap).build())
        }
        printing?.print(printables)
    }
    private fun centerImageHorizontally(bitmap: Bitmap, receiptWidth: Int): Bitmap {
        val imageWidth = bitmap.width
        val padding = (receiptWidth - imageWidth) / 2
        val centeredBitmap = Bitmap.createBitmap(receiptWidth, bitmap.height, bitmap.config)
        val canvas = Canvas(centeredBitmap)
        canvas.drawColor(Color.WHITE) // Set the background color of the receipt
        canvas.drawBitmap(bitmap, padding.toFloat(), 0f, null)
        return centeredBitmap
    }
    override fun onBackPressed() {
        if (status=="accepted"||status=="rejected"){
            onBackPressedDispatcher.onBackPressed()
            handler?.removeCallbacks(runnable!!)
            finish()
        }else{
        startActivity(Intent(this, OrderStatusActivity::class.java))
            handler?.removeCallbacks(runnable!!)
            finish()
    }
    }
    fun buildRecycler(){
        binding.recyclerMain.layoutManager=LinearLayoutManager(this)
        var adapter= MainAdapterTwo(courseModelArrayList!!,this)
        binding.recyclerMain.adapter=adapter
    }
    private fun rejectOrder(orderId: String) {
        var sharedPreferences=getSharedPreferences("MyPrefs",0)
        var userid=sharedPreferences.getString("userId","")
        var jwt=sharedPreferences.getString("token","")
        updateOrderStatus(orderId, userid!!, "Rejected", jwt!!)
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
    private  var acceptButton: Button?=null
    private var rejectButton: Button?=null
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
    @SuppressLint("SuspiciousIndentation")
    fun getOrders(userId: String, token: String) {
    var  pd = ProgressDialog(this)
           pd!!.setMessage("Loading!...")
           pd?.setCanceledOnTouchOutside(false)
           pd!!.show()
        val baseUrl = "https://api.freeorder.co.uk/api/Order/getbyuserid/$userId"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(baseUrl)
            .header("Authorization", "Bearer $token")
            .get()
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OrderManxagerss", "Failed to get orders. Error: ${e.message}")
                // Handle failure, show error to the user, etc.
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    try {
                        val orders = JSONArray(responseData)
                        // val jsonObject = JSONObject(responseData)

                        if (orders.length()>0) {
                            val orderObj = orders.getJSONObject(orders.length()-1)
                            val orderId = orderObj.getInt("id")
                            val orderDate = orderObj.getString("orderDate")
                            var status = orderObj.getString("status")
                            val description = orderObj.getString("description")
                            val paymentMethod = orderObj.getString("paymentMethod")
                            val orderingMethod = orderObj.getString("orderingMethod")
//                            var tokens = jsonObject.getString("token")
//                            Log.e("OrderMasdnager", tokens)
                            // Extract customer details
                            val customerObj = orderObj.getJSONObject("customer")
                            val firstName = customerObj.getString("first_Name")
                            val lastName = customerObj.getString("last_Name")
                            val phone = customerObj.getString("phone")
                            val email = customerObj.getString("email")
                            val city = customerObj.getString("city")
                            val postCode = customerObj.getString("postCode")
                            var address1 = customerObj.getString("address1")

                            // extract order details
                            val orderDetailsArray = orderObj.getJSONArray("orderDetails")
                            val items = ArrayList<OrderShow>() // Create a list to hold the order items
                            var order:Orderss?=null
                            if (status=="Order Placed"){
                                 order = Orderss(
                                    orderId.toString(),
                                    orderDate,
                                    status,
                                    description,
                                    paymentMethod,
                                    orderingMethod,
                                    firstName,
                                    lastName,
                                    phone,
                                    email,
                                    items,
                                    orderDetailsArray.length()
                                )
                            }
                            val menuItemIds = ArrayList<String>()
                            val menuItemNames = ArrayList<String>()
                            val prices = ArrayList<String>()
                            val quantities = ArrayList<String>()
                            val totalPrices = ArrayList<String>()

                            for (j in 0 until orderDetailsArray.length()) {
                                val orderDetails = orderDetailsArray.getJSONObject(j)
                                val menuItemId = orderDetails.getInt("menuItemId")
                                val menuItemName = orderDetails.getString("menuItemName")
                                val price = orderDetails.getDouble("price")
                                val quantity = orderDetails.getInt("quantity")
                                val totalPrice = orderDetails.getDouble("totalPrice")


                                menuItemIds.add(menuItemId.toString())
                                menuItemNames.add(menuItemName)
                                prices.add(price.toString())
                                quantities.add(quantity.toString())
                                totalPrices.add(totalPrice.toString())
                                Log.e("OrderManxagerss", menuItemName.toString())
                                courseModelArrayList?.add(order!!)

                                editor?.putString("id", orderId.toString())
                                editor?.putString("orderDate", orderDate.toString())
                                editor?.putString("status", status.toString())
                                editor?.putString("description", description.toString())
                                editor?.putString("paymentMethod", paymentMethod.toString())
                                editor?.putString("orderingMethod", orderingMethod.toString())
                                editor?.putString("firstName", firstName.toString())
                                editor?.putString("lastName", lastName.toString())
                                editor?.putString("phone", phone.toString())
                                editor?.putString("email", email.toString())
                                editor?.putString("menuItemId", menuItemId.toString())
                                editor?.putString("totalPrice", totalPrice.toString())
                                editor?.putString("city", city)
                                editor?.putString("postcode",postCode)
                                editor?.putString("address", address1)


                            }
                            var name=menuItemNames.toString()
                            var id=menuItemIds.toString()
                            var pric=prices.toString()
                            var quan=quantities.toString()
                            var total=totalPrices.toString()
                            Log.e("OrderManxageress", name.toString())
                            editor?.putString("menuItemName", name)
                            editor?.putString("price", pric)
                            editor?.putString("quantity", quan)
                            editor?.apply()
                            var input=name
                            val cleanString=input?.removeSurrounding("[","]")
                            val itemName=cleanString?.split(", ")

                            var inputQuantity =quan
                            val cleanStringQuantity=inputQuantity?.removeSurrounding("[","]")
                            val itemsQuantity=cleanStringQuantity?.split(", ")

                            val cleanPrice=pric?.removeSurrounding("[","]")
                            val itemPrice=cleanPrice?.split(", ")

                            if (itemName != null && itemsQuantity != null) {
                                for (index in itemName.indices) {
                                    val item = itemName[index]
                                    val quantity = itemsQuantity.getOrNull(index) ?: ""
                                    val prices = itemPrice?.getOrNull(index) ?: ""
                                    val orderItem = OrderShow(id, item.toString(), prices, quantity, total)
                                    items.add(orderItem)
                                }
                            }

                            runOnUiThread {
                                val cleanString = totalPrices.toString().removeSurrounding("[", "]")
                                val convert = cleanString.split(", ")
                                val totalPrice: String = if (convert.size > 1) {
                                    val sum = convert.map { it.toFloatOrNull() ?: 0.0f }.sum()
                                    sum.toString()
                                } else {
                                    convert.firstOrNull() ?: ""
                                }
                                binding.totalPrices.text= totalPrice
                            }
                            Log.e("OrderManxagerss", paymentMethod + orderingMethod + phone)
                        }
                        runOnUiThread {
                           buildRecycler()
                           pd?.dismiss()
                            // Update RecyclerView adapter with the orderList
                            // ...
                        }
                    } catch (e: JSONException) {
                        Log.e("OrderManxagerss", "Error parsing JSON response: ${e.message}")
                        // Handle parsing error, show error to the user, etc.
                    }
                } else {

                    Log.e("OrderManxagerss", "Failed to get orders. Status: ${response.code}")
                    // Handle API error, show error to the user, etc.
                }
            }
        })
    }

}
