package com.wingspan.androidassignment.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.wingspan.androidassment.R


object Singleton {

    fun View.setDebouncedClickListener(debounceTimeMillis:Long=500, onClick:()->Unit){
        val handler= Handler(Looper.getMainLooper())
        var lastClickTime=0L
        setOnClickListener {
            val currentTime= System.currentTimeMillis()
            if(currentTime-lastClickTime>=debounceTimeMillis){
                lastClickTime=currentTime
                handler.post(){
                    onClick()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context):Boolean
    {
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
    @SuppressLint("MissingInflatedId", "InflateParams")
    fun showNetworkAlertDialog(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout=inflater.inflate(R.layout.alertdialog_network_connection,null)
        var dialog= AlertDialog.Builder(context).setView(layout).setCancelable(false).create()
        val close=layout.findViewById<AppCompatButton>(R.id.close)
        close.setOnClickListener(){
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.white_bg))
        dialog.show()

    }
}