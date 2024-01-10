package com.example.android_level_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.android_level_2.databinding.ActivityAddContactBinding


// TODO Класс не используется!

// Класс использовался для отображения формы добавления нового контакта
// После нажатия кнопки SAVE, передавал данные обратно в MainActivity
// через контракт registerForActivityResult
// НЕ используется, ввиду использования для этой же цели DialogFragment
class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private var avatarImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBarFunction()
        saveNewUserData()
        addContactImage()

        fillAllFields()     // TODO DELETE AFTER TEST
    }

    // добавление картинки/аватарки нового контакта
    private fun addContactImage() {

        val addContactImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if (it.resultCode == RESULT_OK) {
                avatarImageUri = it?.data?.data

                Log.d("TAG", "AddContactActivity > AVATAR SET > $avatarImageUri")       // TODO DELETE
                binding.imgAvatar.setImageURI(avatarImageUri)
            }
        })

        binding.imgAddImageIcon.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }  // работает и ACTION_GET_CONTENT
            addContactImageResult.launch(photoPickerIntent)
        }

    }

//  возврат на предидущее Activity
    private fun initToolBarFunction() {
        binding.customToolbarAddContact.imgBackToolbarAddContact.setOnClickListener {
            finish()
        }
    }

    // обработка нажатия кнопки SAVE
    private fun saveNewUserData() {
        binding.btnSaveUserData.setOnClickListener {

            val resultIntent = Intent().apply {
                putExtra("userName", binding.etUserName.text.toString())
                putExtra("userCareer", binding.etCareer.text.toString())
                putExtra("userEmail", binding.etEmail.text.toString())
                putExtra("userPhoneNumber", binding.etPhone.text.toString())
                putExtra("userAddress", binding.etAddress.text.toString())
                putExtra("userBirthday", binding.etBirthday.text.toString())
                putExtra("userImage", avatarImageUri.toString())
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    // TODO DELETE AFTER TEST
    fun fillAllFields(){
        binding.imgFillFields.setOnClickListener {
            binding.etUserName.setText("Roman")
            binding.etCareer.setText("Florist")
            binding.etEmail.setText("test_mail@gmail.com")
            binding.etPhone.setText("066-123-45-67")
            binding.etAddress.setText("Ukraine, Kiev")
            binding.etBirthday.setText("12/01/1990")
        }
    }
}
