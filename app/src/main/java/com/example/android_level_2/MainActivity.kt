package com.example.android_level_2

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_level_2.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), CustomDialog.DialogButtonListener {

    private lateinit var binding: ActivityMainBinding
    private var rvAdapter: ContactAdapter? = null
    private lateinit var viewModel: MainViewModel

    private var snackbar: Snackbar? = null
    private var snackbarVisibility = false
    private var snackbarTimer = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBarFunction()
        initAddContactsButton()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // отображение сообщения на каждый тик (раз в 1 секунду)
        viewModel.onTickTimerMessage.observe(this) { currentValue ->
            snackbar?.setText("Restore contact? ($currentValue sec)")
            snackbarTimer = currentValue
        }

        // по окончанию таймера, скрываем Snackbar
        viewModel.onFinishTimer.observe(this) {status ->
            if (status) {
                snackbar?.let {
                    it.dismiss()
                    snackbar = null
                    snackbarVisibility = false
                }
            }
        }

        // слушатель нажатия кнопки "удалить" (на элементе RecyclerView)
        // при нажатии удаляем контакт (запоминая его данные), показываем Snackbar с возможностью восстановить
        // если контакт решено не сохранять, удаляем данные о нем, иначе восстанавливаем контакт
        val listener = object : ContactAdapter.ElementListener {
            override fun onElementDeleteClick(position: Int) {
                rvAdapter?.removeContact(position)

                if (snackbar != null) {
                    viewModel.timerStop()
                    snackbar = null
                }
                snackbar = createSnackbar(snackbarTimer)
                snackbar?.show()
                snackbarVisibility = true
                viewModel.timerStart()
            }
        }

        rvAdapter = ContactAdapter(viewModel.getContactList(), listener)
        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        binding.rvContacts.adapter = rvAdapter

    }

    // вешаем слушателя для кнопки "Add contact". Открываем диалоговое окно, по нажатию
    private fun initAddContactsButton() {
        binding.tvAddNewContact.setOnClickListener {
            CustomDialog().show(supportFragmentManager, "CustomDialogFragment")
        }
    }

    // Добавление слушателей и реализация функций нажатия кнопок в ToolBar меню
    private fun initToolBarFunction() {
        binding.toolbarContactList.imgBackToolbarContactList.setOnClickListener {
            finish()
        }
        binding.toolbarContactList.imgSearchToolbarContactList.setOnClickListener {
            Toast.makeText(this, getString(R.string.toolbar_search_button_text), Toast.LENGTH_SHORT).show()
        }
    }

    // добавление нового контакта в список
    override fun buttonSaveListener(newUser: User) {
        rvAdapter?.addNewContact(newUser)
    }

    // создание и первичная инициализация Snackbar
    private fun createSnackbar(currentTimer: Int): Snackbar? {
        val rootView = binding.clActivityMainBasicLayout as View
        var snackbar: Snackbar? = Snackbar.make(rootView,"", Snackbar.LENGTH_SHORT)
        snackbar?.setText("Restore contact? ($currentTimer sec)")
        snackbar?.setDuration(Snackbar.LENGTH_INDEFINITE)
        snackbar?.setActionTextColor(getColor(R.color.orange_color))
        snackbar?.setAction(getString(R.string.snackbar_button_text)) {
            snackbar?.dismiss()
            rvAdapter?.restoreContact()
            viewModel.timerStop()
            snackbarVisibility = false
            snackbar = null
        }
        return snackbar
    }

    // сохраняем состояние Snackbar (видимость и оставшееся время отсчета) при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("snackbarVisibility", snackbarVisibility)
        outState.putInt("snackbarTimer", snackbarTimer)
    }

    // восстанавливаем состояние Snackbar (видимость и оставшееся время отсчета) после поворота
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        snackbarVisibility = savedInstanceState.getBoolean("snackbarVisibility")
        snackbarTimer = savedInstanceState.getInt("snackbarTimer")

        if (snackbarVisibility) {
            snackbar = createSnackbar(snackbarTimer)
            snackbar?.show()
            snackbarVisibility = true
        }
    }
    // сохранение во ViewModel данных об удаленном пользователе
    private fun saveTemporaryDeletedData() {
        viewModel.temporaryUserData = rvAdapter?.temporaryContactData
        viewModel.temporaryUserPosition = rvAdapter?.temporaryContactPosition!!
    }

    // восстановление данных об удаленном пользователе
    private fun loadTemporaryDeletedData() {
        rvAdapter?.temporaryContactData = viewModel.temporaryUserData
        rvAdapter?.temporaryContactPosition = viewModel.temporaryUserPosition
    }

    override fun onStart() {
        super.onStart()
        loadTemporaryDeletedData()
    }

    override fun onStop() {
        super.onStop()
        saveTemporaryDeletedData()
    }

}