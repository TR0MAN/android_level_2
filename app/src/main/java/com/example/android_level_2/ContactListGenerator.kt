package com.example.android_level_2

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

class ContactListGenerator {

    private val firstNameList = listOf(
        "Anna", "Barbara", "Camilla", "Eva", "Florance", "Gloria", "Helga", "Iris", "Jessica",
        "Kimberly", "Lesley", "Margaret", "Nicole", "Olivia", "Polly", "Rebecca", "Sandra",
        "Tiffany", "Veronica", "Wendy", "Zoe"
    )

    private val lastNameList = listOf(
        "Adams", "Barnes", "Campbell", "Davidson", "Evans", "Farmer", "Garrett", "Hampton",
        "Jefferson", "Kelley", "Lambert", "Massey", "Nicholson", "Osborne", "Patterson", "Randall",
        "Savage", "Tucker", "Vincent", "Walton", "Young", "Zimmerman"
    )

    private val profession = listOf(
        "Actress", "Biologist", "Chemist", "Dentist", "Engineer", "Florist", "Graphic Designer",
        "Hairstylist", "Journalist", "Interpreter", "Librarian", "Milkman", "Nurse", "Optometrist",
        "Programmer", "Research Scientist", "Singer", "Teacher", "Voice Actor", "Waitress", "Zoologist"
    )

    private val avatar = listOf(
        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cGVyc29ufGVufDB8fDB8fHww",
        "https://images.unsplash.com/photo-1593104547489-5cfb3839a3b5?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mjh8fHBlcnNvbnxlbnwwfHwwfHx8MA%3D%3D",
        "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MzZ8fHBlcnNvbnxlbnwwfHwwfHx8MA%3D%3D",
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NzV8fHBlcnNvbnxlbnwwfHwwfHx8MA%3D%3D",
        "https://images.unsplash.com/photo-1588701177361-c42359b29f68?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTIzfHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1592755219588-d4ff92a0d4de?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MzYyfHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1475823678248-624fc6f85785?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MzU0fHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1567468219153-4b1dea5227ea?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MzY2fHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1564463836146-4e30522c2984?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTk0fHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1615473967657-9dc21773daa3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTI2fHxwZXJzb258ZW58MHx8MHx8fDA%3D",
        "https://images.unsplash.com/photo-1525875975471-999f65706a10?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTAyfHxwZXJzb258ZW58MHx8MHx8fDA%3D"
    )

    // генерация списка из контакт-листа
    fun getContactList(): MutableList<User> {

        val contactList = (0..firstNameList.size).map {
            User(
                userName = "${firstNameList[Random.nextInt(firstNameList.size)]} ${lastNameList[Random.nextInt(lastNameList.size)]}",
                userCareer = profession[Random.nextInt(profession.size)],
                userImage = avatar[it % avatar.size]
            )
        }
        return contactList as MutableList
    }

}