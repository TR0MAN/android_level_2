package com.example.android_level_2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_level_2.databinding.ElementContactViewBinding

class ContactAdapter(list: MutableList<User>, listener: ElementListener) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    interface ElementListener {
        fun onElementDeleteClick(position: Int)
    }

    private var contactList: MutableList<User> = list

    var temporaryContactData: User? = null
    var temporaryContactPosition: Int = 0

    // приходящий в конструктор слушатель
    private val onElementClickListener:ElementListener = listener

    // Вариант №2 создания Holder
    //    class ContactHolder(val binding: ElementContactViewBinding) : RecyclerView.ViewHolder(binding.root) {}

    inner class ContactHolder(element: View) : RecyclerView.ViewHolder(element) {
        val binding = ElementContactViewBinding.bind(element)
        fun bind(user: User) {
            binding.tvContactName.text = user.userName
            binding.tvContactCareer.text = user.userCareer
            Glide.with(binding.imgContactAvatar.context)                                            // v.2 можно передавать context при создании, вместе со списком
                .load(user.userImage)
                .circleCrop()
                .placeholder(R.drawable.default_avatar)
                .into(binding.imgContactAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_contact_view, parent, false)
        return ContactHolder(view)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.bind(contactList[position])

        holder.binding.imgContactDelete.setOnClickListener {
          onElementClickListener.onElementDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    // добавление нового контакта в общий список контактов
    fun addNewContact(user: User) {
        contactList.add(user)
        notifyItemInserted(contactList.size+1)
    }

    fun removeContact(position: Int) {
        temporaryContactData = contactList[position]
        temporaryContactPosition = position

        contactList.removeAt(position)
        notifyItemRemoved(position)                                         // работает, но весь список не обновляется, каждый элемент сохраняет свою прежнюю позицию
                                                                            // есть (15, 16, 17, 18 элемент), удаляем 17, получаем список (15, 16, 18)
    }

    // восстановление контакта
    fun restoreContact(){
        contactList.add(temporaryContactPosition, temporaryContactData!!)
        notifyItemInserted(temporaryContactPosition)
    }

}

//Другие методы оповещения:
//notifyItemChanged(int),
//notifyItemInserted(int),
//notifyItemRemoved(int),
//notifyItemRangeChanged(int, int),
//notifyItemRangeInserted(int, int),
//notifyItemRangeRemoved(int, int)