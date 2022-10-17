package com.smart.watchkart.ui.cart

import com.smart.watchkart.model.ProductModel
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.smart.watchkart.R
import com.google.android.material.imageview.ShapeableImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class CartAdapter(
    private val dataModelList: ArrayList<ProductModel>,
    private val fragment: CartFragment
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fragment.baseActivity!!.setImageWithGlide(
            holder.productImage,
            dataModelList[holder.adapterPosition].product_Image
        )
        holder.brandName.text = dataModelList.get(holder.adapterPosition).brand_Name
        holder.productName.text = dataModelList.get(holder.adapterPosition).product_Name
        holder.productPrice.text = "$ " + fragment.intSeparatorBy3(
            "" + dataModelList.get(holder.adapterPosition).get_price()
        )
        holder.quantity.text = dataModelList.get(holder.adapterPosition).get_quantity().toString()
        holder.decreaseQuantity.setOnClickListener(View.OnClickListener {
            fragment.decreaseQuantity(
                dataModelList[holder.adapterPosition].id, holder.adapterPosition
            )
        })
        holder.increaseQuantity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                fragment.increaseQuantity(dataModelList[holder.adapterPosition].id)
            }
        })
    }

    override fun getItemCount(): Int {
        return dataModelList.size
    }

    fun removeItem(adapterPosition: Int) {
        dataModelList.removeAt(adapterPosition)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ShapeableImageView
        var productName: AppCompatTextView
        var brandName: AppCompatTextView
        var productPrice: AppCompatTextView
        var quantity: AppCompatTextView
        var productConstraint: ConstraintLayout
        var decreaseQuantity: AppCompatImageView
        var increaseQuantity: AppCompatImageView

        init {
            productImage = itemView.findViewById(R.id.productImage)
            productName = itemView.findViewById(R.id.productNameTv)
            brandName = itemView.findViewById(R.id.brandNameTv)
            productPrice = itemView.findViewById(R.id.productPriceTv)
            quantity = itemView.findViewById(R.id.totalQuantity)
            productConstraint = itemView.findViewById(R.id.productConstraint)
            decreaseQuantity = itemView.findViewById(R.id.decreaseQuantity)
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity)
        }
    }
}