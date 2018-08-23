package macaxeira.com.emprestado.features.listitem

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import macaxeira.com.emprestado.R

class FilterDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = activity?.layoutInflater?.inflate(R.layout.dialog_filter, container)

        val title = activity?.getString(R.string.filter)
        dialog.setTitle(title?.toUpperCase())

        return view
    }
}