package io.github.ravindragv.smsledger

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.ravindragv.smsledger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mTelephonyPermissionGranted = false
    private val mSMSReceiver = SMSReceiver()

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val READ_TELEPHONY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        requestPermissionsToReadSMS()
    }

    private fun requestPermissionsToReadSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
            == PackageManager.PERMISSION_GRANTED) {
            mTelephonyPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                READ_TELEPHONY
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_TELEPHONY) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mTelephonyPermissionGranted = true
            } else {
                Toast.makeText(this, "Need Telephony permissions to read SMS!!",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

}