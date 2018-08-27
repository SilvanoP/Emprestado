package macaxeira.com.emprestado.features.listitem

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import macaxeira.com.emprestado.R

class FilterDialog : DialogFragment() {

    companion object {

        @JvmStatic
        val IS_MINE = "IS_MINE"

        @JvmStatic
        fun newInstance(isMine: Boolean): FilterDialog {
            val dialog = FilterDialog()
            val args = Bundle()
            args.putBoolean(IS_MINE, true)
            dialog.arguments = args

            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = requireActivity().layoutInflater?.inflate(R.layout.dialog_filter, container)

        val title = requireActivity().getString(R.string.filter)
        dialog.setTitle(title.toUpperCase())

        return view
    }

    interface FilterDialogListener {
        fun filter()
    }
}