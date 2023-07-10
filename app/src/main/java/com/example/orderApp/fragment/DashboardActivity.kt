package com.example.orderApp.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.orderApp.R
import com.example.orderApp.adapter.DashboardAdapter
import com.example.orderApp.model.OrderItem
import com.example.orderApp.model.Orders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class DashboardActivity : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        var adapter: DashboardAdapter? = null
        var courseModelArrayList: ArrayList<Orders>? = null
        var pd: ProgressDialog? = null
        var sharedPreferences: SharedPreferences? = null
        var userId: String? = null
        var jwt: String? = null
        private var noOrders: ImageView? = null
        private var txtNoOrders: TextView? = null
        private var recyclerDashboard:RecyclerView?=null
        private var swipeRefreshLayout:SwipeRefreshLayout?=null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v=inflater.inflate(R.layout.activity_dashboard,container,false)
        recyclerDashboard=v.findViewById(R.id.recycler_dashboard)
        noOrders=v.findViewById(R.id.no_orders)
        txtNoOrders=v.findViewById(R.id.txt_no_orders)
        swipeRefreshLayout=v.findViewById(R.id.swipeMain)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                val token = task.result
                Log.d("adeferf",token)
            })
        swipeRefreshLayout?.setOnRefreshListener {
            refreshData()
        }
        courseModelArrayList = ArrayList()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", 0)
        userId = sharedPreferences!!.getString("userId", "")
        jwt = sharedPreferences!!.getString("token", "")
        var fcm = sharedPreferences!!.getString("fcmToken", "")
        var email = sharedPreferences!!.getString("email", "")
        var password = sharedPreferences!!.getString("password", "")
        Log.d("jkkjst", userId.toString())
        Log.d("adeferf", jwt.toString())
        getOrders(userId!!, jwt!!)

        return v
    }

    private fun refreshData() {
        courseModelArrayList?.clear()
        getOrders(userId!!, jwt!!)
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefreshLayout?.isRefreshing = false
            //requireActivity().recreate()
        }, 1000)

    }

        private fun buildRecyclerView() {
            adapter = DashboardAdapter(courseModelArrayList!!, requireActivity())
            val manager = LinearLayoutManager(requireActivity())
            recyclerDashboard?.setHasFixedSize(true)
            recyclerDashboard?.adapter = adapter
            recyclerDashboard?.layoutManager = manager
            adapter?.notifyItemRangeRemoved(0, courseModelArrayList?.size!!);

//            adapter?.notifyItemRangeInserted(0, courseModelArrayList?.size!!);
//            recyclerDashboard?.scrollToPosition(courseModelArrayList?.size!! - 1);
            if (courseModelArrayList?.isEmpty() == true) {
                noOrders?.visibility = View.VISIBLE
                txtNoOrders?.visibility = View.VISIBLE
            } else {
               noOrders?.visibility = View.GONE
               txtNoOrders?.visibility = View.GONE
            }
            pd?.dismiss()
        }
        // Rest of the code remains the same

    fun getOrders(userId: String, token: String) {
        pd = ProgressDialog(requireActivity())
        pd!!.setMessage("Loading!...")
        pd?.setCanceledOnTouchOutside(false)
        pd!!.show()
        Handler().postDelayed(Runnable {
            loginUser()
        },500)
        val baseUrl = "https://api.freeorder.co.uk/api/Order/getbyuserid/$userId"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(baseUrl)
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OrderManager", "Failed to get orders. Error: ${e.message}")
                // Handle failure, show error to the user, etc.
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    try {
                        val orders = JSONArray(responseData)
                       // val jsonObject = JSONObject(responseData)

                        for (i in 0 until orders.length()) {
                            val orderObj = orders.getJSONObject(i)
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

                            // extract order details
                            val orderDetailsArray = orderObj.getJSONArray("orderDetails")
                            val items = ArrayList<OrderItem>() // Create a list to hold the order items
                            if (status=="Order Placed"){
                                val order = Orders(
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
                                courseModelArrayList?.add(order)
                                requireActivity().runOnUiThread {
                                    adapter?.notifyDataSetChanged()
                                }
                            }
                            val menuItemIds = ArrayList<String>()
                            val menuItemNames = ArrayList<String>()
                            val prices =ArrayList<String>()
                            val quantities =ArrayList<String>()
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
                                Log.e("OrderMasaanager", totalPrice.toString())
                            }

                            val orderItem = OrderItem(menuItemIds, menuItemNames, prices, quantities, totalPrices)
                            items.add(orderItem)

                            Log.e("OrderManager", paymentMethod + orderingMethod + phone)
                        }

                        // Pass the orderList to the RecyclerView adapter and update UI accordingly
                        requireActivity().runOnUiThread {
                            buildRecyclerView()
                            // Update RecyclerView adapter with the orderList
                            // ...
                        }
                    } catch (e: JSONException) {
                        Log.e("OrderManager", "Error parsing JSON response: ${e.message}")
                        // Handle parsing error, show error to the user, etc.
                    }
                } else {
                    requireActivity().runOnUiThread {
                        loginUser()
                    }
                    Log.e("OrderManagerss", "Failed to get orders. Status: ${response.code}")
                    // Handle API error, show error to the user, etc.
                }
            }
        })
    }
    fun loginUser() {
        var sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", 0)
        var email = sharedPreferences!!.getString("email", "")
        var password = sharedPreferences!!.getString("password", "")
        val baseUrl = "http://api.freeorder.co.uk/api/auth"
        val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
        urlBuilder?.addQueryParameter("email", email)
        urlBuilder?.addQueryParameter("password", password)
        val url = urlBuilder?.build().toString()
        val client = OkHttpClient()
        Log.e("UserLoginManager", "$email $password")

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("FCMToken", sharedPreferences?.getString("fcmToken", "")!!)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UserLoginManager", "Failed to make API request: ${e.message}")
                // Handle failure, show error to the user, etc.
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                try {
                    val jsonObject = JSONObject(responseData)
                    val status = response.code
                    if (status == 200) {
                        // Rest of your code
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    // Log.w(AcceptOrderActivity.TAG, "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                val token = task.result
                                var tokens = jsonObject.getString("token")
                                var userId = jsonObject.getString("userId")
                                Log.d("adasdd", tokens)
                                val editor = sharedPreferences?.edit()
                                editor?.putBoolean("isLoggedIn", true)
                                editor?.putString("token", tokens)
                                editor?.putString("userId", userId)
                                editor?.putString("fcm", token)
                                editor?.apply()
                            })
                    } else {
                        // Rest of your code
                        // Login failed, handle the error
                        val errors = jsonObject.getJSONObject("errors")
                        val emailErrors = errors.getJSONArray("email")
                        val passwordErrors = errors.getJSONArray("password")

                        // Log or display the error messages
                        for (i in 0 until emailErrors.length()) {
                            Log.e("UserLoginManager", "Email error: ${emailErrors.getString(i)}")
                        }

                        for (i in 0 until passwordErrors.length()) {
                            Log.e("UserLoginManager", "Password error: ${passwordErrors.getString(i)}")
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("UserLoginManager", "Error parsing JSON response: ${e.message}")
                    // Handle parsing error, show error to the user, etc.
                }
            }
        })
    }
}