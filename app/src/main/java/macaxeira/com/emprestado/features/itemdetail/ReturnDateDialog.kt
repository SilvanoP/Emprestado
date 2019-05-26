package macaxeira.com.emprestado.features.itemdetail

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.*

class ReturnDateDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {

        const val CALENDAR_PARAM = "CALENDAR_PARAM"

        @JvmStatic
        fun newInstance(cal: Calendar): ReturnDateDialog {
            val dialog = ReturnDateDialog()
            val args = Bundle()
            args.putSerializable(CALENDAR_PARAM, cal)

            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = arguments?.getSerializable(CALENDAR_PARAM) as Calendar

        val pickerDialog = DatePickerDialog(requireActivity(), this, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        pickerDialog.datePicker.minDate = System.currentTimeMillis()
        return pickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val listener = activity as ReturnDateDialogListener
        listener.onDateSet(year, month, dayOfMonth)
    }

    interface ReturnDateDialogListener {
        fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
    }
}