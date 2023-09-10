package android.tvz.hr.finalproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.tvz.hr.finalproject.view.LoginActivity
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.model.Utility
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class EditWeatherActivity : AppCompatActivity() {
    private lateinit var docId: String
    private lateinit var noteTitleEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var tempEditText: EditText
    private lateinit var humidityEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var windEditText: EditText
    private lateinit var cloudsEditText: EditText
    private lateinit var pressureEditText: EditText
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_edit_weather)

        // Retrieve the data passed from the WeatherDetailsActivity
        val extras = intent.extras
        if (extras != null) {
            docId = extras.getString("docId", "")
            val noteTitle = extras.getString("noteTitle", "")
            val city = extras.getString("city", "")
            val temp = extras.getString("temp", "")
            val humidity = extras.getString("humidity", "")
            val description = extras.getString("description", "")
            val wind = extras.getString("wind", "")
            val clouds = extras.getString("clouds", "")
            val pressure = extras.getString("pressure", "")

            Log.i("noteTitle", noteTitle.toString())

            noteTitleEditText = findViewById(R.id.note_title_edit_text)
            cityEditText = findViewById(R.id.city_edit_text)
            tempEditText = findViewById(R.id.temp_edit_text)
            humidityEditText = findViewById(R.id.humidity_edit_text)
            descriptionEditText = findViewById(R.id.description_edit_text)
            windEditText = findViewById(R.id.wind_edit_text)
            cloudsEditText = findViewById(R.id.clouds_edit_text)
            pressureEditText = findViewById(R.id.pressure_edit_text)
            saveButton = findViewById(R.id.save_button)

            noteTitleEditText.setText(noteTitle)
            cityEditText.setText(city)
            tempEditText.setText(temp)
            humidityEditText.setText(humidity)
            descriptionEditText.setText(description)
            windEditText.setText(wind)
            cloudsEditText.setText(clouds)
            pressureEditText.setText(pressure)

            saveButton.setOnClickListener {
                saveWeatherData()
            }
        }
    }

    fun showMenu(view: android.view.View) {
        val menuBtn = findViewById<ImageButton>(R.id.menuBtnEdit)
        val popupMenu = PopupMenu(this@EditWeatherActivity, menuBtn)

        popupMenu.menu.add(resources.getString(R.string.logout))
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val builder = AlertDialog.Builder(this)

            builder.setMessage(resources.getString(R.string.que))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@EditWeatherActivity, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                }
                .create()
                .show()
            true
        }
    }

    private fun saveWeatherData() {

        val updatedNoteTitle = noteTitleEditText.text.toString().trim()
        val updatedCity = cityEditText.text.toString().trim()
        val updatedTemp = tempEditText.text.toString().trim()
        val updatedHumidity = humidityEditText.text.toString().trim()
        val updatedDescription = descriptionEditText.text.toString().trim()
        val updatedWind = windEditText.text.toString().trim()
        val updatedClouds = cloudsEditText.text.toString().trim()
        val updatedPressure = pressureEditText.text.toString().trim()

        val weatherRef = Utility.getCollectionReferenceForWeather().document(docId)

        weatherRef.update(
            "noteTitle", updatedNoteTitle,
            "cityName", updatedCity,
            "temp", updatedTemp,
            "humidity", updatedHumidity,
            "description", updatedDescription,
            "wind", updatedWind,
            "clouds", updatedClouds,
            "pressure", updatedPressure
        ).addOnSuccessListener {
            Toast.makeText(this@EditWeatherActivity,
                resources.getString(R.string.data), Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {exception ->
            Log.e("EditWeatherActivity", "Failed to update database", exception)
            Toast.makeText(this@EditWeatherActivity,
                resources.getString(R.string.data_err), Toast.LENGTH_SHORT).show()
        }
    }
}