package com.example.android_level_2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.android_level_2.databinding.ActivityAddContactBinding
import java.lang.ClassCastException


// Нужно делать через DialogFragment() из-за того, что он правильно управляет жизненным циклом диалога
// и восстанавливает его после поворота (диалог вовремя скрывется и правильно восстанавливается после поворота)
// при создании через AlertDialog.Builder после поворота диалог пропадет
class CustomDialog: DialogFragment() {

    interface DialogButtonListener {
        fun buttonSaveListener(newUser: User)
    }

    private lateinit var dialogListener: DialogButtonListener
    private lateinit var addContactImageResult: ActivityResultLauncher<Intent>
    private val binding by lazy { ActivityAddContactBinding.inflate(LayoutInflater.from(context)) }
    private var avatarImageUri: Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // инициализируем слушателя
        try {
            dialogListener = activity as DialogButtonListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement DialogButtonListener")
        }

        // регистрация "контракта" после отработки startActivityForResult с получением данных
        addContactImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                avatarImageUri = it?.data?.data

                binding.imgAvatar.setImageURI(avatarImageUri)
            }
        })
    }

    // восстанавливаем выбранную пользователем картинку (после поворота)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (avatarImageUri == null) {
            avatarImageUri = savedInstanceState?.getString("image")?.toUri()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // вставляем картинку в ImageView (после поворота), если она есть
        avatarImageUri?.let { binding.imgAvatar.setImageURI(it) }

//        binding = ActivityAddContactBinding.inflate(LayoutInflater.from(context))                     (либо можно инициализировать и тут)
        var dialog: AlertDialog? = null
        val dialogBuilder2 = AlertDialog.Builder(context)
        dialogBuilder2.setView(binding.root)

        // клик по кнопке "выбрать фото"
        binding.imgAddImageIcon.setOnClickListener {
            addContactImage()
        }

        // клик по кнопке SAVE
        binding.btnSaveUserData.setOnClickListener {
            dialogListener.buttonSaveListener(saveNewUserData())
            dialog?.cancel()
        }

        // клик по стрелке "НАЗАД" в ToolBar
        binding.customToolbarAddContact.imgBackToolbarAddContact.setOnClickListener {
            dialog?.cancel()
        }

        // иконка заполнения данными полей
        binding.imgFillFields.setOnClickListener { fillAllFields()  }

        dialog = dialogBuilder2.create()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    // добавление картинки/аватарки нового контакта
    private fun addContactImage() {
        // работает ACTION_OPEN_DOCUMENT и ACTION_GET_CONTENT
        val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
        addContactImageResult.launch(photoPickerIntent)
    }

    // обработка нажатия кнопки SAVE
    private fun saveNewUserData() :User {
        return User(
            userName = binding.etUserName.text.toString(),
            userCareer = binding.etCareer.text.toString(),
            userEmail = binding.etEmail.text.toString(),
            userPhoneNumber = binding.etPhone.text.toString(),
            userAddress = binding.etAddress.text.toString(),
            userBirthday = binding.etBirthday.text.toString(),
            userImage = avatarImageUri.toString()
        )
    }

    // автоматическое заполнение полей страницы с добавлением контакта, но можно и в ручную
    private fun fillAllFields() {
        binding.etUserName.setText("Roman")
        binding.etCareer.setText("Florist")
        binding.etEmail.setText("test_mail@gmail.com")
        binding.etPhone.setText("066-123-45-67")
        binding.etAddress.setText("Ukraine, Kiev")
        binding.etBirthday.setText("12/01/1990")
    }

    // сохраняем выбранную пользователм картинку (при повороте, до кнопки SAVE)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (avatarImageUri != null) {
            outState.putString("image", avatarImageUri.toString())
        }
    }
}

