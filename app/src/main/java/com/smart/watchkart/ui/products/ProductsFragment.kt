package com.smart.watchkart.ui.products

import com.smart.watchkart.base.BaseFragment
import com.smart.watchkart.utils.OrderPlacedCallBack
import com.smart.watchkart.utils.SearchCallBack
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.smart.watchkart.ui.addProduct.AddProductFragment
import com.smart.watchkart.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.smart.watchkart.databinding.FragmentProductsBinding
import com.smart.watchkart.ui.productDetail.ProductDetailFragment
import com.smart.watchkart.model.ProductModel
import java.lang.Exception
import java.util.*

class ProductsFragment() : BaseFragment<FragmentProductsBinding?>(), OrderPlacedCallBack,
    SearchCallBack {
    private var adapter: ProductAdapter? = null
    private var currentPosition = 0
    private var isListLoaded = false
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProductsBinding {
        return FragmentProductsBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProductListing()
        userFromDatabase
        baseActivity!!.currentDrawerPosition = 0
        adapter = ProductAdapter(baseActivity!!.productList, this)
        binding!!.productRecyclerView.adapter = adapter
        binding!!.addProductBtn.setOnClickListener {
            changeFragment(
                true,
                AddProductFragment(),
                R.id.homeContainer
            )
        }
    }

    private val userFromDatabase: Unit
        private get() {
            val rootNode = FirebaseDatabase.getInstance()
            val reference = rootNode.getReference(getString(R.string.root_name))
            val checkUser = reference.orderByChild("userName").equalTo(
                pref.getUserName(
                    baseActivity!!
                )
            )
            checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nameFromDb = snapshot.child(pref.getUserName(baseActivity!!).toString()).child("name").getValue(String::class.java)
                        binding?.userNameEt?.text = "Welcome\n($nameFromDb)"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    fun viewProduct(id: Int) {
        for (i in baseActivity!!.productList.indices) {
            if (id == baseActivity!!.productList[i].id) {
                baseActivity?.selectedProductPosition = i
                changeFragment(true, ProductDetailFragment(), R.id.homeContainer)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        baseActivity!!.setOrderPlacedCallBack(this)
        baseActivity!!.searchCallBack = this
        baseActivity!!.findViewById<View>(R.id.menuIcon).visibility = View.VISIBLE
        baseActivity!!.findViewById<View>(R.id.searchView).visibility = View.VISIBLE
        baseActivity!!.findViewById<View>(R.id.cartIcon).visibility = View.VISIBLE
        if (pref.getIsAdmin(baseActivity!!)) {
            binding!!.addProductBtn.visibility = View.VISIBLE
        }
        try {
            if (!baseActivity!!.searchText!!.isEmpty()) {
                filterProduct(baseActivity!!.searchText)
            }
        } catch (ex: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        baseActivity!!.findViewById<View>(R.id.menuIcon).visibility =
            View.GONE
        baseActivity!!.findViewById<View>(R.id.searchView).visibility =
            View.GONE
        baseActivity!!.findViewById<View>(R.id.cartIcon).visibility =
            View.GONE
    }

    override fun orderSuccess() {
        val size = baseActivity!!.productList.size
        for (position in 0 until size) {
            if (baseActivity!!.productList[position].get_is_AddedTo_Cart()) {
                baseActivity!!.productList[position].set_quantity(1)
                baseActivity!!.productList[position].set_is_AddedTo_Cart(false)
            }
        }
    }

    private fun filterProduct(searchString: String?) {
        if (!baseActivity!!.productList.isEmpty()) {
            val itemList = baseActivity!!.productList
            val temp: ArrayList<ProductModel> = ArrayList<ProductModel>()
            for (i in itemList.indices) {
                val productName = itemList[i].product_Name
                val serialNumber = itemList[i].serial_Number
                if (productName!!.contains(searchString!!.trim { it <= ' ' }
                        .uppercase(Locale.getDefault())) || serialNumber!!.contains(
                        searchString.trim { it <= ' ' }.uppercase(Locale.getDefault())
                    )
                ) {
                    temp.add(itemList[i])
                }
            }
            if (temp.size > 0) {
                binding!!.productRecyclerView.visibility = View.VISIBLE
                binding!!.notFound.visibility = View.GONE
                adapter!!.updateAdapter(temp)
            } else {
                binding!!.productRecyclerView.visibility = View.GONE
                binding!!.notFound.visibility = View.VISIBLE
            }
        }
    }

    override fun searchProduct(query: String?) {
        filterProduct(query)
    }

    override fun onAddProduct() {
        adapter!!.notifyDataSetChanged()
    }

    private fun setProductListing() {
        val rootNode = FirebaseDatabase.getInstance()
        val reference = rootNode.reference.child(getString(R.string.products_root_name))
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isListLoaded) {
                    if (snapshot.childrenCount == 0L) {
                        binding!!.notFound.visibility = View.VISIBLE
                        binding!!.notFound.text = "No data found"
                        binding!!.productRecyclerView.visibility = View.GONE
                    } else {
                        baseActivity!!.productList.clear()
                        for (postSnapshot in snapshot.children) {
                            val model = postSnapshot.getValue(
                                ProductModel::class.java
                            )
                            baseActivity!!.productList.add(model!!)
                            adapter!!.notifyItemInserted(currentPosition)
                            currentPosition++
                        }
                        isListLoaded = true
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}