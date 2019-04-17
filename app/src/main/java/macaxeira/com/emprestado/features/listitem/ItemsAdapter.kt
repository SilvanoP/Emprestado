package macaxeira.com.emprestado.features.listitem

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.listitem_main.view.*
import macaxeira.com.emprestado.R
import macaxeira.com.emprestado.data.entities.Item
import macaxeira.com.emprestado.data.entities.Person
import macaxeira.com.emprestado.utils.CircleTransform
import macaxeira.com.emprestado.utils.Utils

class ItemsAdapter(private val context: Context, var items: MutableList<Item>, private val listener: ItemsAdapterListener) :
        RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    val selectedItems = SparseBooleanArray()

    // Animation related attributes
    private val animationItemsIndex = SparseBooleanArray()
    private var currentSelectedIndex = -1
    private var reverseAllAnimations = false

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
        currentSelectedIndex = position
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
            animationItemsIndex.delete(position)
        } else {
            selectedItems.put(position, true)
            animationItemsIndex.put(position, true)
        }

        notifyItemChanged(position)
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItems(deletedItems: List<Item>) {
        items.removeAll(deletedItems)
        notifyDataSetChanged()
    }

    fun restoreItem(item: Item, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: Item) {
            itemView.listItemDescriptionText.text = item.description

            var returnedText = item.returnDate
            if (TextUtils.isEmpty(returnedText)) {
                returnedText = context.getString(R.string.empty_date)
            } else if (item.isReturned) {
                returnedText = context.getString(R.string.returned)
            }

            itemView.listItemMainReturnDate.text = returnedText
            val person: Person? = item.person

            val personName = person?.name ?: ""
            var personText: String = if (item.isMine) context.getString(R.string.to) else context.getString(R.string.from)
            personText += ": $personName"

            itemView.listItemPersonText.text = personText

            if (person != null && person.photoUri.isNotEmpty()) {
                Picasso.get().load(Uri.parse(person.photoUri))
                        .transform(CircleTransform())
                        .into(itemView.listItemPersonPhotoImage)
            } else {
                Picasso.get().load(R.drawable.bg_circle)
                        .into(itemView.listItemPersonPhotoImage)
            }

            itemView.isActivated = selectedItems[adapterPosition, false]

            applyClickEvents(adapterPosition)
            applyIconAnimation(adapterPosition)
        }

        private fun applyClickEvents(position: Int) {
            itemView.listItemTypeImage.setOnClickListener {
                listener.onIconClicked(position)
            }

            itemView.listItemBackImage.setOnClickListener {
                listener.onIconClicked(position)
            }

            itemView.listItemBodyContainer.setOnClickListener {
                listener.onClickItem(position)
            }

            itemView.listItemBodyContainer.setOnLongClickListener {
                listener.onLongClickItem(adapterPosition)
                it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                true // Return true
            }
        }

        private fun applyIconAnimation(position: Int) {
            if (selectedItems.get(position, false)) {
                itemView.listItemTypeImage.visibility = View.GONE
                resetIconYAxis(itemView.listItemBackImage)
                itemView.listItemBackImage.visibility = View.VISIBLE
                itemView.listItemBackImage.alpha = 1F
                if (currentSelectedIndex == position) {
                    Utils.flipView(context, itemView.listItemBackImage, itemView.listItemTypeImage, true)
                    resetCurrentIndex()
                }
            } else {
                itemView.listItemBackImage.visibility = View.GONE
                resetIconYAxis(itemView.listItemTypeImage)
                itemView.listItemTypeImage.visibility = View.VISIBLE
                itemView.listItemTypeImage.alpha = 1F
                if((reverseAllAnimations && animationItemsIndex.get(position, false)) ||
                        currentSelectedIndex == position) {
                    Utils.flipView(context, itemView.listItemBackImage, itemView.listItemTypeImage, false)
                    resetCurrentIndex()
                }
            }
        }

        private fun resetIconYAxis(view: View) {
            if (view.rotationY != 0F) {
                view.rotationY = 0F
            }
        }

    }

    interface ItemsAdapterListener {
        fun onClickItem(position: Int)
        fun onLongClickItem(position: Int)
        fun onIconClicked(position: Int)
    }
}