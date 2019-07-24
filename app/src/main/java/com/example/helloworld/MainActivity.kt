package com.example.helloworld

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.EditText
import java.io.File
import java.io.FileWriter
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat

const val EXTRA_MESSAGE = "com.example.helloworld.MESSAGE"
const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    fun isReadable(view: View) {
        val isReadable = isExternalStorageReadable()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "is readable: $isReadable")
        }
        startActivity(intent)
    }

    fun isWritable(view: View) {
        val isWritable = isExternalStorageWritable()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "is writable: $isWritable")
        }
        startActivity(intent)
    }

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /* Checks if external storage is available to at least read */
    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    private fun getPublicAlbumStorageDir(albumName: String): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file?.mkdirs()) {
            Log.e("info", "Directory not created")
        }
        return file
    }

    fun selectFile(view: View) {

        val flag = checkPermission(WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        if (flag) {
            writeFile()
        }

    }

    fun pdf2Image(view: View) {
        
        supportFragmentManager.beginTransaction()
            .add(R.id.container, PdfRendererBasicFragment(), "pdf_renderer_basic")
            .commit()
        /*val flag = checkPermission(WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        if (flag) {

            val dir = getPublicAlbumStorageDir("")
            val pdf = File("$dir/pdu2.pdf")
            val readBytes = pdf.readBytes()

            println(readBytes)

        }*/

//        var result: String? = ""
//        var fileReader: FileReader? = null
//        var bufferedReader: BufferedReader? = null



    }

    private fun checkPermission(permission: String, code: Int): Boolean {
        var flag = false
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    code
                )

            }
        } else {
            flag = true
        }
        return flag

    }

    private fun writeFile() {
        val dir = getPublicAlbumStorageDir("")

        val file = File("$dir/test_file.txt")

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
            Log.i("info", "create dir")
        }
        if (!file.exists()) {
            file.createNewFile()
            Log.i("info", "create txt file")

        }
        val fw = FileWriter(file, true)
        fw.write("this is for test.")
        fw.close()
        Log.i("info", "write message success")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
