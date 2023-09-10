package android.tvz.hr.finalproject.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.tvz.hr.finalproject.view.LoginActivity
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.controller.WeatherAdapter
import android.tvz.hr.finalproject.model.Utility
import android.tvz.hr.finalproject.model.Weather
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query


class WeatherDetailsActivity : AppCompatActivity(),
    WeatherAdapter.OnEditButtonClickListener,
    WeatherAdapter.OnDeleteButtonClickListener {
//class WeatherDetailsActivity : AppCompatActivity() {

    private lateinit var weatherAdapter: WeatherAdapter

    val weather = Weather()
    var isEditMode: Boolean = false
    var isItemClickEnabled: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_weather_details)

        val recyclerView : RecyclerView = findViewById(R.id.recycler_view)
        var pageTitle = findViewById<TextView>(R.id.weather_update_text_view)

        weather.noteTitle = intent.getStringExtra("noteTitle")

        var docId = intent.getStringExtra("docId")

         if(docId!= null && docId.isNotEmpty()) {
             isEditMode = true
         }

        if(isEditMode) {
            //pageTitle.setText("Edit List Title")
            isItemClickEnabled = false
        }

        val collectionReference = Utility.getCollectionReferenceForWeather()

        val query: Query = collectionReference.orderBy("noteTitle", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Weather> = FirestoreRecyclerOptions.Builder<Weather>()
            .setQuery(query, Weather::class.java)
            .build()

        recyclerView.layoutManager = LinearLayoutManager(this)
        //weatherAdapter = WeatherAdapter(options, this, this)
        weatherAdapter = WeatherAdapter(options, this, resources, this, this)
        weatherAdapter.setItemClickEnabled(isItemClickEnabled)
        //recyclerView.setAllowClickWhenDisabled(isItemClickEnabled)
        recyclerView.setAdapter(weatherAdapter)
    }

    fun showMenu(view: android.view.View) {
        val menuBtn = findViewById<ImageButton>(R.id.menuBtnDetail)
        val popupMenu = PopupMenu(this@WeatherDetailsActivity, menuBtn)

        popupMenu.menu.add("Logout")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Do you really want to log out ?")
                .setPositiveButton("Yes") {dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@WeatherDetailsActivity, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("No") {dialog, which ->
                }
                .create()
                .show()
            true
        }
    }

    override fun onDeleteButtonClick(position: Int) {

        val docId = weatherAdapter.snapshots.getSnapshot(position).id
        val documentReference = Utility.getCollectionReferenceForWeather().document(docId)
        Log.i("deletebutton", docId)

        /*Toast.makeText(this@WeatherDetailsActivity,
            "POZICIJA DELETE $position", Toast.LENGTH_SHORT).show()*/
        documentReference.delete()
            .addOnSuccessListener {
                Toast.makeText(this@WeatherDetailsActivity,
                    "Weather deleted successfully.", Toast.LENGTH_SHORT).show()
                weatherAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {e->
                Toast.makeText(this@WeatherDetailsActivity,
                    "Failed to delete weather: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onEditButtonClick(position: Int) {


        val docId = weatherAdapter.snapshots.getSnapshot(position).id
        /*Toast.makeText(this@WeatherDetailsActivity,
            "POZICIJA EDIT $position", Toast.LENGTH_SHORT).show()*/
        Log.i("editbutton", docId)
        val weather = weatherAdapter.getItem(position)

        val intent = Intent(this, EditWeatherActivity::class.java)
        intent.putExtra("docId", docId)
        intent.putExtra("noteTitle", weather.noteTitle)
        intent.putExtra("city", weather.cityName)
        intent.putExtra("temp", weather.temp)
        intent.putExtra("humidity", weather.humidity)
        intent.putExtra("description", weather.description)
        intent.putExtra("wind", weather.wind)
        intent.putExtra("clouds", weather.clouds)
        intent.putExtra("pressure", weather.pressure)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        weatherAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        weatherAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        weatherAdapter.notifyDataSetChanged()
    }
}