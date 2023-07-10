package com.mazenrashed.printooth.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.R
import com.mazenrashed.printooth.data.DiscoveryCallback
import com.mazenrashed.printooth.databinding.ActivityScanningBinding
import com.mazenrashed.printooth.utilities.Bluetooth
import java.util.*

class ScanningActivity : AppCompatActivity() {
    private lateinit var bluetooth: Bluetooth
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var adapter: BluetoothDevicesAdapter
    lateinit var binding: ActivityScanningBinding
    private val REQUEST_BLUETOOTH_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BluetoothDevicesAdapter(this)
        bluetooth = Bluetooth(this)
        setup()
    }

    private fun setup() {
        initViews()
        initListeners()
        initDeviceCallback()
    }

    private fun initDeviceCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {
            override fun onDiscoveryStarted() {
                binding.refreshLayout.isRefreshing = true
                binding.toolbar.title = "Scanning.."
                devices.clear()
                devices.addAll(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
                binding.toolbar.title = if (!devices.isEmpty()) "Select a Printer" else "No devices"
                binding.refreshLayout.isRefreshing = false
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onDevicePaired(device: BluetoothDevice) {
                Printooth.setPrinter(device.name, device.address)
                Toast.makeText(this@ScanningActivity, "Device Paired", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            }

            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(this@ScanningActivity, "Device unpaired", Toast.LENGTH_SHORT).show()
                val pairedPrinter = Printooth.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    Printooth.removeCurrentPrinter()
                devices.remove(device)
                adapter.notifyDataSetChanged()
                bluetooth.startScanning()
            }

            override fun onError(message: String) {
                Toast.makeText(this@ScanningActivity, "Error while pairing", Toast.LENGTH_SHORT).show()

                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initListeners() {
        binding.refreshLayout.setOnRefreshListener { bluetooth.startScanning() }
        binding.printers.setOnItemClickListener { _, _, i, _ ->
            val device = devices[i]
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                Printooth.setPrinter(device.name, device.address)
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            } else if (device.bondState == BluetoothDevice.BOND_NONE)
                bluetooth.pair(devices[i])
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViews() {
        binding.printers.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Check if the app has the required permissions
        if (permissions.all {
                ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            // Permissions are already granted, start Bluetooth scanning here
            runBluetoothScanning()
        } else {
            // Request the permissions from the user
            ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_BLUETOOTH_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, start Bluetooth scanning
                runBluetoothScanning()
            } else {
                // Permissions denied, handle accordingly (e.g., show an error message)
                Toast.makeText(this, "Bluetooth scanning permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun runBluetoothScanning() {
        bluetooth.onStart()
        if (!bluetooth.isEnabled)
            bluetooth.enable()
        Handler().postDelayed({
            bluetooth.startScanning()
        }, 1000)
    }



    override fun onStop() {
        super.onStop()
        bluetooth.onStop()
    }

    companion object {
        const val SCANNING_FOR_PRINTER = 115
    }

    inner class BluetoothDevicesAdapter(context: Context) : ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_1) {
        override fun getCount(): Int {
            return devices.size
        }

        @SuppressLint("MissingPermission")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return LayoutInflater.from(context)
                    .inflate(R.layout.bluetooth_device_row, parent, false).apply {
                        findViewById<TextView>(R.id.name).text = if (devices[position].name.isNullOrEmpty()) devices[position].address else devices[position].name
                        findViewById<TextView>(R.id.pairStatus).visibility = if (devices[position].bondState != BluetoothDevice.BOND_NONE) View.VISIBLE else View.INVISIBLE
                        findViewById<TextView>(R.id.pairStatus).text = when (devices[position].bondState) {
                            BluetoothDevice.BOND_BONDED -> "Paired"
                            BluetoothDevice.BOND_BONDING -> "Pairing.."
                            else -> ""
                        }
                        findViewById<ImageView>(R.id.pairedPrinter).visibility = if (Printooth.getPairedPrinter()?.address == devices[position].address) View.VISIBLE else View.GONE
                    }
        }
    }
}
