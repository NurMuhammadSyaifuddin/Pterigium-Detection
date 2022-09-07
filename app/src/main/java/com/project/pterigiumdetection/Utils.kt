package com.project.pterigiumdetection

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.pterigiumdetection.databinding.LayoutBottomPickBinding

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.gone(){
    visibility = View.GONE
}

fun showBottomSheetDialogUploadImage(context: Context, listenerPickCamera: () -> Unit, listenerPickGallery: () -> Unit): BottomSheetDialog{
    val binding = LayoutBottomPickBinding.inflate(LayoutInflater.from(context), null, false)

    val dialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
        setCancelable(true)
        dismissWithAnimation = true
    }

    binding.apply {
        btnPickCamera.setOnClickListener {
            dialog.dismiss()
            listenerPickCamera()
        }
        btnPickGallery.setOnClickListener {
            dialog.dismiss()
            listenerPickGallery()
        }
    }

    return dialog
}

fun Context.showToast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun ImageView.loadImage(url: String){
    Glide.with(this.context)
        .load(Uri.parse(url.replace("file:", "")))
        .placeholder(R.color.gray)
        .into(this)
}