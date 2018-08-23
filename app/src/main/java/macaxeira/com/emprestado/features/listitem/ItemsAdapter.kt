package macaxeira.com.emprestado.features.listitem

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_main.view.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item

class ItemsAdapter(val context: Context, var items: MutableList<Item>): RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.listitem_main, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: Item) {
            itemView.listItemDescriptionText.text = item.description
            itemView.listItemMainReturnDate.text = item.returnDate
        }

    }

}