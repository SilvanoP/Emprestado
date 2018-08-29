package macaxeira.com.emprestado.features.itemdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import macaxeira.com.emprestado.data.entities.ItemType

class TypesListAdapter(context: Context, val res: Int, val types: List<ItemType>):
        ArrayAdapter<ItemType>(context, res, types) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(res, null)
        val textView = view as TextView

        val name = context.getString(types[position].res)
        textView.text = name

        return view
    }
}