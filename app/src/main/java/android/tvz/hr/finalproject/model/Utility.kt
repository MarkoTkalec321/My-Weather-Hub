package android.tvz.hr.finalproject.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class Utility {

    companion object{
        fun getCollectionReferenceForWeather(): CollectionReference {
            val currentUser = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("weather")
                .document(currentUser!!.uid).collection("my_weather")
        }
    }

}