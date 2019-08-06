package macaxeira.com.emprestado.features.listitem

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import macaxeira.com.emprestado.R

class FilterDialog : DialogFragment(), View.OnClickListener {

    companion object {

        @JvmStatic
        val FILTER_OPTION = "FILTER_OPTION"

        @JvmStatic
        fun newInstance(filterOption: Int): FilterDialog {
            val dialog = FilterDialog()
            val args = Bundle()
            args.putInt(FILTER_OPTION, filterOption)
            dialog.arguments = args

            return dialog
        }
    }

    private var checkedId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_filter, container)
        val option = arguments?.getInt(FILTER_OPTION)

        // Since synthetic components don't work on dialog, I do it the old way
        val radioLoan = view?.findViewById<RadioGroup>(R.id.dialogFilterRadioBorrowedLent)
        val radioButtonAll = view?.findViewById(R.id.dialogFilterButtonAll) as RadioButton
        val filterButton = view.findViewById<Button>(R.id.dialogFilterFilterButton)
        val cancelButton = view.findViewById<Button>(R.id.dialogFilterCancelButton)

        radioLoan?.setOnCheckedChangeListener {
            _, checkedId ->
            this.checkedId = checkedId
            if(checkedId != -1) {
                filterButton.isEnabled = true
            }
        }

        radioLoan?.clearCheck()
        if (option != null && option >= 0) {
            radioLoan?.check(option)
        } else {
            radioLoan?.check(radioButtonAll.id)
            //radioButtonAll.isChecked = true
        }

        filterButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.dialogFilterCancelButton) {
            dialog.cancel()
        } else if (v?.id == R.id.dialogFilterFilterButton) {
            val listener = requireActivity() as FilterDialogListener
            listener.filter(checkedId)
            dialog.dismiss()
        }
    }

    interface FilterDialogListener {
        fun filter(filter: Int)
    }
}