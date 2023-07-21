package com.example.orderApp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.orderApp.R
import com.example.orderApp.auth.LoginActivity
import com.example.orderApp.databinding.ActivitySettingBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.converter.ArabicConverter
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    private var printing : Printing? = null
    private lateinit var sharedPreferences: SharedPreferences
    var pd: ProgressDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        Printooth.init(this)
        checkPermissions(this)
        pd= ProgressDialog(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.toolSetting.setNavigationOnClickListener {
           onBackPressed()
        }

        binding.btnLogout.setOnClickListener {
            var alertDialog=AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Logout your current account!")
                .setPositiveButton("ok",DialogInterface.OnClickListener { dialogInterface, i ->
                    if (Printooth.hasPairedPrinter())
                        Printooth.removeCurrentPrinter()

                    sharedPreferences.edit().remove("isLoggedIn").commit()
                    sharedPreferences.edit().remove("fcmToken").apply()

                          /*  FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("deleted", "successfully")
                                } else {
                                    Log.d("deleted","un successfully")
                                }
                            }*/

                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                })
                .setNegativeButton("cancel",null)
            alertDialog.show()
        }


        binding.btnUnpair.setOnClickListener {
            var alertDialog=AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Unpair bluetooth printer!")
                .setPositiveButton("ok",DialogInterface.OnClickListener { dialogInterface, i ->
                    if (Printooth.hasPairedPrinter())
                        Printooth.removeCurrentPrinter()
                    Toast.makeText(this@SettingActivity,"Unpaired Successfully!",Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("cancel",null)
            alertDialog.show()
        }

        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()
    }

    private fun initListeners() {
        /* binding.printOrder.setOnClickListener {
             if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                 ScanningActivity::class.java),
                 ScanningActivity.SCANNING_FOR_PRINTER)
             else printSomePrintable()
         }*/
        binding.btnpair.setOnClickListener {
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            else
                Toast.makeText(this@SettingActivity,"Already Paired!",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@SettingActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                pd?.dismiss()
                Toast.makeText(this@SettingActivity, "Failed to connect with printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@SettingActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@SettingActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                pd?.dismiss()
                //        Toast.makeText(this@AcceptOrderActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val REQUEST_PERMISSION_CODE = 1001

    fun checkPermissions(activity: Activity): Boolean {
        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE

        val writePermissionGranted = ContextCompat.checkSelfPermission(
            activity,
            writePermission
        ) == PackageManager.PERMISSION_GRANTED

        val readPermissionGranted = ContextCompat.checkSelfPermission(
            activity,
            readPermission
        ) == PackageManager.PERMISSION_GRANTED

        val permissions = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissions.add(writePermission)
        }
        if (!readPermissionGranted) {
            permissions.add(readPermission)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
            return false
        }

        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices) {
                    val permission = permissions[i]
                    val grantResult = grantResults[i]
                    if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE &&
                        grantResult == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permission granted, perform necessary operations
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, OrderStatusActivity::class.java))
        finish()
    }
}