package com.smart.watchkart.ui.products

import com.smart.watchkart.model.ProductModel
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.smart.watchkart.R
import com.google.android.material.imageview.ShapeableImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class ProductAdapter(
    private var dataModelList: ArrayList<ProductModel>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    fun updateAdapter(products: ArrayList<ProductModel>) {
        fragment.baseActivity!!.runOnUiThread {
            dataModelList = products
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fragment.baseActivity!!.setImageWithGlide(
            holder.productImage,
            dataModelList[holder.adapterPosition].product_Image
        )
        holder.brandName.text = dataModelList[holder.adapterPosition].brand_Name
        holder.productName.text = dataModelList[holder.adapterPosition].product_Name
        holder.serialNumber.text = dataModelList[holder.adapterPosition].serial_Number
        holder.productPrice.text =
            "$ " + fragment.intSeparatorBy3("" + dataModelList[holder.adapterPosition].get_price())
        holder.productConstraint.setOnClickListener { fragment.viewProduct(dataModelList[holder.adapterPosition].id) }
    }

    override fun getItemCount(): Int {
        return dataModelList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ShapeableImageView
        var productName: AppCompatTextView
        var brandName: AppCompatTextView
        var productPrice: AppCompatTextView
        var serialNumber: AppCompatTextView
        var productConstraint: ConstraintLayout

        init {
            productImage = itemView.findViewById(R.id.productImage)
            productName = itemView.findViewById(R.id.productNameTv)
            brandName = itemView.findViewById(R.id.brandNameTv)
            productPrice = itemView.findViewById(R.id.productPriceTv)
            serialNumber = itemView.findViewById(R.id.serialNumberTv)
            productConstraint = itemView.findViewById(R.id.productConstraint)
        }
    }
}