package com.project.pterigiumdetection.detection

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.WHITE
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.project.pterigiumdetection.*
import com.project.pterigiumdetection.databinding.ActivityDetectionBinding
import com.project.pterigiumdetection.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding

    private var uri: Uri? = null

    private val customCropImage = registerForActivityResult(CropImageContract()) {
        if (it is CropImage.CancelledResult) {
            return@registerForActivityResult
        }
        supportActionBar?.show()
        uri = it.uriContent
        handleCropImageResult(uri.toString())
    }

    private fun handleCropImageResult(uri: String) {
        supportActionBar?.hide()
        binding.apply {
            imgAmbilFotoResult.visible()
            imgAmbilFotoResult.setImageURI(Uri.parse(uri.replace("file:", "")))
            imgAmbilFoto.gone()
            imageCamera.gone()
            textViewAmbilFoto.gone()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permissions, 0)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            imageCamera.setOnClickListener {
                showBottomSheetDialogUploadImage(this@DetectionActivity,
                    {
                        openCamera()
                    },
                    {
                        openGallery()
                    })
                    .show()
            }
            imgAmbilFotoResult.setOnClickListener {
                showBottomSheetDialogUploadImage(this@DetectionActivity,
                    {
                        openCamera()
                    },
                    {
                        openGallery()
                    })
                    .show()
            }

            btnPrediksi.setOnClickListener {
                if (imgAmbilFotoResult.drawable == null) {
                    showToast("Anda wajib mengambil gambar terlebih dahulu")
                    return@setOnClickListener
                } else {
                    classifyImage(uri)
                    motionLayout.transitionToState(R.id.langkah3, 1000)
                }
            }
        }
    }

    private fun classifyImage(uri: Uri?) {

        val model = Model.newInstance(applicationContext)

        var image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

        image = Bitmap.createScaledBitmap(image!!, 150, 150, false)

        val byteBuffer = ByteBuffer.allocateDirect(4 * image!!.width * image.height * 3).apply {
            order(ByteOrder.nativeOrder())

            val pixels = IntArray(image.width * image.height)

            rewind()
            image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until 150) {
                for (j in 0 until 150) {
                    val `val` = pixels[pixel++]
                    putFloat(((`val`.shr(16) and 0xFF) - 127) / 255.0f)
                    putFloat(((`val`.shr(8) and 0xFF) - 127) / 255.0f)
                    putFloat(((`val` and 0xFF) - 127) / 255.0f)
                }
            }
        }

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 3), DataType.FLOAT32)

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        val percentNegatif = formatPercent(outputFeature0[0])
        val percentPositif = formatPercent(outputFeature0[1])

        binding.apply {
            tvPercentNegatif.text = getString(R.string.s_negatif, percentNegatif)
            tvPercentPositif.text = getString(R.string.s_positif, percentPositif)

            if (percentPositif > percentNegatif) {
                tvResult.setBackgroundResource(R.drawable.bg_result_red)
                tvResult.text = getString(R.string.positif_pterigium)
            }else{
                tvResult.setBackgroundResource(R.drawable.bg_result_blue)
                tvResult.text = getString(R.string.negatif_pterigium)
            }
        }

        // Releases model resources if no longer used.
        model.close()
    }

    private fun formatPercent(value: Float): String {
        val newValue = value * 100
        return "%.2f".format(newValue) + "%"
    }

    private fun openCamera() {
        startCameraWithoutUri(includeCamera = true, includeGallery = false)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        startCameraWithoutUri(includeCamera = false, includeGallery = true)
    }

    private fun startCameraWithoutUri(includeCamera: Boolean, includeGallery: Boolean) {
        customCropImage.launch(
            options {
                setImageSource(
                    includeGallery = includeGallery,
                    includeCamera = includeCamera,
                )
                // Normal Settings
                setScaleType(CropImageView.ScaleType.FIT_CENTER)
                setCropShape(CropImageView.CropShape.RECTANGLE)
                setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                setAspectRatio(1, 1)
                setMaxZoom(4)
                setAutoZoomEnabled(true)
                setMultiTouchEnabled(true)
                setCenterMoveEnabled(true)
                setShowCropOverlay(true)
                setAllowFlipping(true)
                setSnapRadius(3f)
                setTouchRadius(48f)
                setInitialCropWindowPaddingRatio(0.1f)
                setBorderLineThickness(3f)
                setBorderLineColor(Color.argb(170, 255, 255, 255))
                setBorderCornerThickness(2f)
                setBorderCornerOffset(5f)
                setBorderCornerLength(14f)
                setBorderCornerColor(WHITE)
                setGuidelinesThickness(1f)
                setGuidelinesColor(R.color.white)
                setBackgroundColor(Color.argb(119, 0, 0, 0))
                setMinCropWindowSize(24, 24)
                setMinCropResultSize(20, 20)
                setMaxCropResultSize(99999, 99999)
                setActivityTitle("")
                setActivityMenuIconColor(0)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setOutputCompressQuality(90)
                setRequestedSize(0, 0)
                setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                setInitialCropWindowRectangle(null)
                setInitialRotation(0)
                setAllowCounterRotation(false)
                setFlipHorizontally(false)
                setFlipVertically(false)
                setCropMenuCropButtonTitle(null)
                setCropMenuCropButtonIcon(0)
                setAllowRotation(true)
                setNoOutputImage(false)
                setFixAspectRatio(false)
            }
        )
    }

}