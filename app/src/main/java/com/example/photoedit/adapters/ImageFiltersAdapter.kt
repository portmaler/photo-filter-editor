package com.example.photoedit.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.photoedit.R
import com.example.photoedit.data.ImageFilter
import com.example.photoedit.databinding.ItemContainerFilterBinding
import com.example.photoedit.listeners.ImageFilterListener

class ImageFiltersAdapter(
    private val imageFilters: List<ImageFilter>,
    private val imageFilterListener: ImageFilterListener
    ) : RecyclerView.Adapter<ImageFiltersAdapter.ImageFilterViewHolder>() {

    private var selectedFilterPosition = 0
    private var previouslySelectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFilterViewHolder {
        val binding = ItemContainerFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageFilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageFilterViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder){
            with(imageFilters[position]){
                binding.imagefilterpreviews.setImageBitmap(filterPreview)
                binding.textfiltername.text = name
                binding.root.setOnClickListener {
                  if(position != selectedFilterPosition){
                      imageFilterListener.onFilterSelected(this)
                      previouslySelectedPosition = selectedFilterPosition
                      selectedFilterPosition = position
                      with(this@ImageFiltersAdapter){
                          notifyItemChanged(previouslySelectedPosition, Unit)
                          notifyItemChanged(selectedFilterPosition, Unit)
                      }
                  }
                }
            }
            binding.textfiltername.setTextColor(
                ContextCompat.getColor(
                    binding.textfiltername.context,
                    if(selectedFilterPosition == position)
                        R.color.purple_500
                    else
                        R.color.prymarytext
                )
            )
        }
    }

    override fun getItemCount() = imageFilters.size

    inner class ImageFilterViewHolder(val binding: ItemContainerFilterBinding) :
        RecyclerView.ViewHolder(binding.root)
}