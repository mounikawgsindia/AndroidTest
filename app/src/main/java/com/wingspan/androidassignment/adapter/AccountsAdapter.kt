package com.wingspan.androidassignment.adapter


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wingspan.androidassignment.model.AccountsResponseModel
import com.wingspan.androidassignment.viewmodel.AccountsViewModel
import com.wingspan.androidassment.databinding.CustomAccountsBinding

class AccountsAdapter(val context: Context,
                      var notificationslist: ArrayList<AccountsResponseModel>,
                      var viewModel: AccountsViewModel
): RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomAccountsBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root= CustomAccountsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        Log.d("TargetActivity", "Notification: adapter ${notificationslist.size}")
        return notificationslist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val dataList=notificationslist[position]
            name.text="ActName : "+dataList.ActName
            id.text="ACTID : "+dataList.actid
        }
    }



    private fun alertDialog(message: String?) {
        Log.d("alert dialog","alertDialog $message")
        val builder= AlertDialog.Builder(context)
        builder .setCancelable(true)
            .setTitle("Notification")
            .setMessage(message)
            .setPositiveButton("OK"){dialog,_->
                dialog.dismiss()
            }
        val alertDialog=builder.create()
        alertDialog.show()

    }
    // Method to update data

    fun updateData(newData: List<AccountsResponseModel>) {
        // Update the contacts list and notify the adapter
        notificationslist = newData as ArrayList<AccountsResponseModel>
        notifyDataSetChanged() // Notify the RecyclerView that the data has changed
    }
    fun deleteItem(position:Int){
        if(position>=0 && position<notificationslist.size){
            notificationslist.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,notificationslist.size)
        }else{
            Log.e("Error", "Invalid position for deletion")
        }

    }
    fun addItem(position: Int,newItem: AccountsResponseModel){
        notificationslist.add(position,newItem)
        notifyItemInserted(position)

    }
}