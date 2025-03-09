package com.wingspan.androidassignment.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wingspan.androidassignment.database.AccountsDataBase
import com.wingspan.androidassignment.extensions.Singleton.setDebouncedClickListener
import com.wingspan.androidassignment.model.AccountsPoint
import com.wingspan.androidassignment.viewmodel.RoomViewModel
import com.wingspan.androidassment.R
import com.wingspan.androidassment.databinding.CustomRoomAccountBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomAccountsAdapter(
    val context: Context,
    var notificationslist: ArrayList<AccountsPoint>,
    var viewModel: RoomViewModel
): RecyclerView.Adapter<RoomAccountsAdapter.ViewHolder>() {
    val accountDao = AccountsDataBase.getDatabase(context).accountsPoints()
    class ViewHolder(val binding: CustomRoomAccountBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root= CustomRoomAccountBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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
            name.text="ActName : "+dataList.AccountName
            id.text="ACTID : "+dataList.AccountId
            altername.text="Alter Name : "+dataList.alterName
            alterNameBtn.setDebouncedClickListener(){
                showSpeechToTextDialog(context,dataList.AccountName,dataList.alterName,dataList.id)
            }
            updateBtn.setDebouncedClickListener {
                updateAlertDialog(context,dataList.AccountName,dataList.AccountId,dataList.alterName,dataList.id)
            }
            deleteBtn.setDebouncedClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val rowsDeleted= accountDao.deleteAccount(dataList.id)
                    if (rowsDeleted > 0) {
                        withContext(Dispatchers.Main) {
                            notificationslist.removeAt(position) // Remove from the list
                            notifyItemRemoved(position) // Notify RecyclerView
                            notifyItemRangeChanged(position, notificationslist.size)
                            Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Delete failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val updatedList = accountDao.getGeoFencingPoints() // Get latest data
                    CoroutineScope(Dispatchers.Main).launch {
                        updateData(updatedList) // Refresh RecyclerView
                    }
                }
            }
        }
    }

    private fun updateAlertDialog(context: Context, accountName: String, accountId: String, alterName: String, id: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_update, null)

        val editActualName = dialogView.findViewById<EditText>(R.id.editActualName)
        val editAccountId = dialogView.findViewById<EditText>(R.id.editAccountId)
        val editAltNAme = dialogView.findViewById<EditText>(R.id.editSpeechToText)

        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // Set existing values
        editActualName.setText(accountName)
        editAccountId.setText(accountId)
        editAltNAme.setText(alterName)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // Prevent accidental dismiss
            .create()



        btnSave.setOnClickListener {

                CoroutineScope(Dispatchers.IO).launch {
                    val rowsUpdated = accountDao.updateAccountDetails(
                        id,
                        editActualName.text.toString(),
                        editAltNAme.text.toString(),
                        editAccountId.text.toString()
                    )
                    withContext(Dispatchers.Main) {
                        if (rowsUpdated > 0) {
                            val updatedList = accountDao.getGeoFencingPoints()
                            updateData(updatedList)
                            Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val updatedList = accountDao.getGeoFencingPoints() // Get latest data
                    CoroutineScope(Dispatchers.Main).launch {
                        updateData(updatedList) // Refresh RecyclerView
                    }
                }


        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun showSpeechToTextDialog(
        context: Context,
        accountName: String,
        alterName: String,
        id: Int
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_speech_to_text, null)

        val tvActualName = dialogView.findViewById<TextView>(R.id.editActualName)

        val editSpeechToText = dialogView.findViewById<EditText>(R.id.editSpeechToText)
        val btnMic = dialogView.findViewById<ImageButton>(R.id.btnMic)
        tvActualName.text= "Account Name : $accountName"
        editSpeechToText.setText(alterName)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Enter Details")
            .setPositiveButton("Save") { _, _ ->
                val actualName = tvActualName.text.toString()

                val speechText = editSpeechToText.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val rowsUpdated = accountDao.updateAccountName(id, speechText) // Update database
                    if (rowsUpdated > 0) {
                        val updatedList = accountDao.getGeoFencingPoints() // Get latest data
                        CoroutineScope(Dispatchers.Main).launch {
                            updateData(updatedList) // Refresh RecyclerView
                            Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val updatedList = accountDao.getGeoFencingPoints() // Get latest data
                    CoroutineScope(Dispatchers.Main).launch {
                        updateData(updatedList) // Refresh RecyclerView
                    }
                }
                Toast.makeText(context, "Saved:\n$actualName\n$speechText\n$speechText", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()


        btnMic.setOnClickListener {
            startSpeechRecognition(context, editSpeechToText)
        }

        dialog.show()
    }
    // Method to update data

    fun updateData(newData: List<AccountsPoint>) {
        // Update the contacts list and notify the adapter
        notificationslist = newData as ArrayList<AccountsPoint>
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
    private fun startSpeechRecognition(context: Context, editText: EditText) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    editText.setText(matches[0]) // Set first recognized text in EditText
                }
            }

            override fun onError(error: Int) {
                Toast.makeText(context, "Speech recognition failed!", Toast.LENGTH_SHORT).show()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

}