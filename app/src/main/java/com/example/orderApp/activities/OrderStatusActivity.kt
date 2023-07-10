package com.example.orderApp.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.example.orderApp.R
import com.example.orderApp.adapter.ViewPagerAdapter
import com.example.orderApp.databinding.ActivityOrderStatusBinding
import com.example.orderApp.fragment.AcceptedFragment
import com.example.orderApp.fragment.DashboardActivity
import com.example.orderApp.model.Orders
import com.example.orderApp.services.NotificationReceiver
import java.util.*
import kotlin.collections.ArrayList


class OrderStatusActivity : AppCompatActivity() {
    var arrayList:ArrayList<String>?=null
    lateinit var binding: ActivityOrderStatusBinding
    private lateinit var beepBroadcastReceiver: NotificationReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beepBroadcastReceiver = NotificationReceiver()
        val intentFilter = IntentFilter("com.example.ACTION_BEEP")
        registerReceiver(beepBroadcastReceiver, intentFilter)

        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter=ViewPagerAdapter(supportFragmentManager)
        var page=(binding.viewPager.adapter as ViewPagerAdapter).getPageTitle(0)
        Log.d("sdsre",page.toString())
    }
    override fun onDestroy() {
        unregisterReceiver(beepBroadcastReceiver)
        super.onDestroy()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // below line is to get our inflater
        val inflater = menuInflater
        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu)
        // below line is to get our menu item.
        val searchItem = menu?.findItem(R.id.actionSearch)
        // getting search view of our item.
        val searchView = searchItem?.actionView as SearchView
        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item2 -> {
                this.finishAffinity()
                true
            }
            R.id.settings->{
                startActivity(Intent(this,SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = java.util.ArrayList<Orders>()

        // running a for loop to compare elements.
        for (item in DashboardActivity.courseModelArrayList!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.firstName.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            DashboardActivity.adapter?.filterList(filteredlist)
        }
    }

}