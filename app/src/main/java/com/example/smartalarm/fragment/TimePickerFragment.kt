package com.example.smartalarm.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment :  DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var dialogListener: TimeDialogListener? = null

    override fun onAttach(context: Context){
        super.onAttach(context)
        dialogListener = context as TimeDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        if (dialogListener != null) dialogListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity as Context, this, hourOfDay, minute, true)
    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        dialogListener?.onDialogTimeSet(tag,hourOfDay, minute)
    }

    interface TimeDialogListener {
        fun onDialogTimeSet(tag: String?, HourOfDay: Int, Minute: Int)
    }
}