package macaxeira.com.emprestado.features.listitem

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.synthetic.main.dialog_filter.*
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
        val view = requireActivity().layoutInflater?.inflate(R.layout.dialog_filter, container)
        val option = arguments?.getInt(FILTER_OPTION)

        dialogFilterRadioBorrowedLent.setOnCheckedChangeListener {
            group, checkedId ->
            this.checkedId = checkedId
        }
        dialogFilterRadioBorrowedLent.clearCheck()
        if (option != null) {
            dialogFilterRadioBorrowedLent.check(option)
        } else {
            dialogFilterButtonAll.isChecked = true
        }

        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == dialogFilterCancelButton.id) {
            dialog.cancel()
        } else if (v?.id == dialogFilterFilterButton.id) {
            val listener = requireActivity() as FilterDialogListener
            listener.filter(checkedId)
        }
    }

    interface FilterDialogListener {
        fun filter(checkedId: Int)
    }
}