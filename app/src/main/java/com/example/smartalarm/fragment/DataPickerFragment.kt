package com.example.smartalarm.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogListener: DateDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = context as DateDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        if (dialogListener != null) dialogListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayofmonth = calendar.get(Calendar.DATE)
        return DatePickerDialog(activity as Context, this, year, month, dayofmonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dialogListener?.onDialogDataSet(tag, year, month, dayOfMonth)
        Log.i(tag, "onDateSet: $year, $month, $dayOfMonth")
    }

    //buat di panggil di activity biar dapet nilai yang sudab dipilih
    interface DateDialogListener {
        fun onDialogDataSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }
}