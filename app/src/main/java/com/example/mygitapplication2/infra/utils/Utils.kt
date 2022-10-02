package com.example.mygitapplication2.infra.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import com.example.mygitapplication2.R

class Utils {

    companion object {

        private var progressDialog: Dialog? = null
        private val TAG = "Utils"

        fun showProgress(context: Context?, isCancelable: Boolean) {
            stopProgress()
            try {
                progressDialog =
                    Dialog(context!!, R.style.NewDialog)
                progressDialog!!.setContentView(R.layout.custom_dialog)
                progressDialog!!.setCancelable(isCancelable)
                //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                progressDialog!!.show()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        fun stopProgress() {
            try {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                    progressDialog = null
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}