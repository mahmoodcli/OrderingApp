package com.example.orderApp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.orderApp.databinding.ActivityAcceptOrderBinding
import com.example.orderApp.fragment.DashboardActivity
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
import java.io.File
import java.io.FileOutputStream

class AcceptOrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityAcceptOrderBinding
    private var printing : Printing? = null
    var pd:ProgressDialog?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Printooth.init(this);
        binding= ActivityAcceptOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd= ProgressDialog(this)
        supportActionBar?.hide()

        binding.toolCompleted.setNavigationOnClickListener {
            onBackPressed()
        }

        checkPermissions(this)
        binding.shareOrder.setOnClickListener {
            val byteArray = intent.getByteArrayExtra("image")
            val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
         shareBitmap(this,bmp,"order")
        }
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initViews()
        initListeners()
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


    private fun initViews() {
        var text = if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
      //  Toast.makeText(this@AcceptOrderActivity, text, Toast.LENGTH_SHORT).show()
    }

    private fun initListeners() {
       /* binding.printOrder.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER)
            else printSomePrintable()
        }*/
        binding.printOrder.setOnClickListener {
            val byteArray = intent.getByteArrayExtra("image")
            val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
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
                Toast.makeText(this@AcceptOrderActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                pd?.dismiss()
                Toast.makeText(this@AcceptOrderActivity, "Failed to connect with printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                pd?.dismiss()
                Toast.makeText(this@AcceptOrderActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                pd?.dismiss()
                Toast.makeText(this@AcceptOrderActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                pd?.dismiss()
        //        Toast.makeText(this@AcceptOrderActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun printSomePrintable() {
        val printables = getSomePrintables()
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

    private fun printSomeImages(bitmap: Bitmap) {
        val receiptWidthInPixels = (80f * 203 / 25.4f).toInt() // Calculate the receipt width in pixels based on 80mm width and 203 DPI
        val centeredBitmap = centerImageHorizontally(bitmap, receiptWidthInPixels)
        val printables = ArrayList<Printable>().apply {
            add(ImagePrintable.Builder(centeredBitmap).build())
        }
        printing?.print(printables)
    }




    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(TextPrintable.Builder()
            .setText(" Hello World : été è à '€' içi Bò Xào Coi Xanh")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        add(TextPrintable.Builder()
            .setText("Hello World : été è à €")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        add(TextPrintable.Builder()
            .setText("Hello World")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        add(TextPrintable.Builder()
            .setText("Hello World")
            .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        add(TextPrintable.Builder()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            val byteArray = intent.getByteArrayExtra("image")
        val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        printSomeImages(bmp)
        initViews()
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
