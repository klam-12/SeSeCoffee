package com.example.sesecoffee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.FragmentEditProductBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.viewModel.ProductsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [EditProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProductFragment : Fragment(R.layout.fragment_edit_product) {
    lateinit var binding: FragmentEditProductBinding
    lateinit var productsViewModel: ProductsViewModel
    lateinit var product : Product

    // since the FragmentEditProductBinding contains arguments in admin_app_graph
//    private val args : EditProductFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        product = args.product!!

        binding.apply {  }
    }




}