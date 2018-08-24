package macaxeira.com.emprestado.features.listitem

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_main.view.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item

class ItemsAdapter(private val context: Context, var items: MutableList<Item>, val listener: ItemsAdapterListener) :
        RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    val selectedItems = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.listitem_main, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun toggleSelection(position: Int) {
        if (selectedItems[position, false]) {
            selectedItems.put(position, false)
        } else {
            selectedItems.put(position, true)
        }

        notifyItemChanged(position)
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        fun bind(item: Item) {
            itemView.listItemDescriptionText.text = item.description
            itemView.listItemMainReturnDate.text = item.returnDate

            itemView.isActivated = selectedItems[adapterPosition, false]
        }

        override fun onLongClick(v: View?): Boolean {
            listener.onLongClickItem(adapterPosition)
            v?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }

    }

    interface ItemsAdapterListener {
        fun onLongClickItem(position: Int)
    }
}