package com.abra.homework_5.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.abra.homework_5.data.CarInfo
import com.abra.homework_5.database.CarInfoDAO
import com.abra.homework_5.database.DataBaseCarInfo
import com.abra.homework_5.R
import com.abra.homework_5.functions.createDirectory
import com.abra.homework_5.functions.saveImage
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

private const val REQUEST_CODE_PHOTO = 1
private const val RESULT_CODE_BUTTON_BACK = 5

class EditCarInfoActivity : AppCompatActivity() {
    private var carId: Long = 0

    private lateinit var textName: EditText
    private lateinit var textProducer: EditText
    private lateinit var textModel: EditText
    private lateinit var imgButtonBack: ImageButton
    private lateinit var imgButtonApply: ImageButton
    private lateinit var fab: FloatingActionButton
    private lateinit var carPhoto: ImageView
    private lateinit var toolbar: Toolbar
    private var photoWasLoaded: Boolean = false
    private lateinit var pathToPicture: String
    private lateinit var carPictureDirectory: File
    private lateinit var currentCarInfo: CarInfo
    private lateinit var database: DataBaseCarInfo
    private lateinit var carInfoDAO: CarInfoDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_car_info)
        toolbar = findViewById(R.id.toolBarEdit)
        carPhoto = findViewById(R.id.imageCarPhoto1)
        textName = findViewById(R.id.etOwnerName1)
        textProducer = findViewById(R.id.etProducer1)
        textModel = findViewById(R.id.etModel1)
        imgButtonBack = findViewById(R.id.imgButtonBack1)
        imgButtonApply = findViewById(R.id.imgButtonApply1)
        fab = findViewById(R.id.fabLoadPhoto1)
        database = DataBaseCarInfo.getDataBase(applicationContext)
        carInfoDAO = database.getCarInfoDAO()
        createDirectoryForPictures()
        setSupportActionBar(toolbar)
        setListeners()
        loadDataFromIntent()
    }

    private fun loadDataFromIntent() {
        val carInfo = intent.getParcelableExtra<CarInfo>("carInfo")
        if (carInfo != null) {
            currentCarInfo = carInfo
            carId = carInfo.id
            val path = carInfo.pathToPicture
            val file = File(path)
            if (file.exists()) {
                if (path == "") {
                    carPhoto.setImageResource(R.drawable.default_icon)
                } else {
                    Glide.with(this).load(path).into(carPhoto)
                    photoWasLoaded = true
                    pathToPicture = path
                }
            }
            textName.setText(carInfo.name)
            textProducer.setText(carInfo.producer)
            textModel.setText(carInfo.model)
        }
    }

    private fun setListeners() {
        imgButtonBack.setOnClickListener {
            backToPreviousActivity()
        }
        imgButtonApply.setOnClickListener {
            editCarInfoAndBackToPreviousActivity()
        }
        fab.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }
    }

    private fun editCarInfoAndBackToPreviousActivity() {
        val name = textName.text.toString()
        val producer = textProducer.text.toString()
        val model = textModel.text.toString()
        if (name.isNotEmpty() && producer.isNotEmpty() && model.isNotEmpty()) {
            if (!photoWasLoaded) {
                pathToPicture = ""
            }
            val carInfo = CarInfo(pathToPicture, name, producer, model).also { it.id = carId }
            carInfoDAO.update(carInfo)
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun backToPreviousActivity() {
        setResult(RESULT_CODE_BUTTON_BACK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.get("data")?.run {
            pathToPicture = saveImage(this as Bitmap, carPhoto, carPictureDirectory)
            photoWasLoaded = true
        }
    }

    private fun createDirectoryForPictures() {
        createDirectory(applicationContext)?.run {
            carPictureDirectory = this
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE_BUTTON_BACK)
        finish()
        super.onBackPressed()
    }
}