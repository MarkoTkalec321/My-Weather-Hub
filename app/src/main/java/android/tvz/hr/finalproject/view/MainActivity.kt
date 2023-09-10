package android.tvz.hr.finalproject.view

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.tvz.hr.finalproject.view.LoginActivity
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.model.Utility
import android.tvz.hr.finalproject.model.Weather
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    val url = "https://api.openweathermap.org/data/2.5/weather"
    val appid = "b4daa2af24b7f5752b0c659a18b75d05"
    val df : DecimalFormat = DecimalFormat("#.##")

    var cityNameS : String? = null
    var tempS : String? = null
    var humidityS : String? = null
    var descriptionS : String? = null
    var windS : String? = null
    var cloudsS : String? = null
    var pressureS : String? = null



    val weather = Weather()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        //val enteredCity = findViewById<EditText>(R.id.city_name)
        val saveBtn = findViewById<com.google.android.material.button.MaterialButton>(R.id.save_btn)
        saveBtn.visibility = View.INVISIBLE

    }

    fun showMenu(view: android.view.View) {
        val menuBtn = findViewById<ImageButton>(R.id.menuBtnMain)
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)

        popupMenu.menu.add(resources.getString(R.string.logout))
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val builder = AlertDialog.Builder(this)

            builder.setMessage(resources.getString(R.string.que))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                }
                .create()
                .show()
            true
        }
    }

    fun myWeatherList(view: android.view.View) {
        startActivity(Intent(view.context, WeatherDetailsActivity::class.java))
    }

    @SuppressLint("CutPasteId")
    fun saveWeatherDetails(view: android.view.View) {

        val noteTitle = findViewById<EditText>(R.id.note_title).text.toString()
        //val cityName = findViewById<EditText>(R.id.city_name).text.toString()

        if(noteTitle == "" || noteTitle!!.isEmpty()) {
            findViewById<EditText>(R.id.note_title).error = resources.getString(R.string.note_title)
        }
        else if (cityNameS == "" || cityNameS!!.isEmpty()) {
            findViewById<EditText>(R.id.city_name).error = resources.getString(R.string.city_name)
        }

        weather.noteTitle = noteTitle
        weather.cityName = cityNameS

        weather.temp = tempS
        weather.humidity = humidityS
        weather.description = descriptionS
        weather.wind = windS
        weather.clouds = cloudsS
        weather.pressure = pressureS

        val documentReference: DocumentReference = Utility.getCollectionReferenceForWeather().document()

        if(noteTitle != "" || noteTitle!!.isNotEmpty() && (cityNameS != "" || cityNameS!!.isNotEmpty())) {
            documentReference.set(weather).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.weat_inf), Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.data_err), Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
        else return


    }

    fun showWeatherDetails(view : android.view.View) {
        var tempUrl = ""
        val cityNameResult = findViewById<TextView>(R.id.city_name_result)
        //val resultTextView = findViewById<TextView>(R.id.text_view_result)
        val city = findViewById<EditText>(R.id.city_name).text.toString().trim()
        var tempResult = findViewById<TextView>(R.id.temp_result)
        var humidityResult = findViewById<TextView>(R.id.humidity_result)
        var descriptionResult = findViewById<TextView>(R.id.description_result)
        var windResult = findViewById<TextView>(R.id.wind_result)
        var cloudsResult = findViewById<TextView>(R.id.clouds_result)
        var pressureResult = findViewById<TextView>(R.id.pressure_result)

        if(city == "") {
            cityNameResult.text = resources.getString(R.string.city_name)
            tempResult.text = ""
            humidityResult.text = ""
            descriptionResult.text = ""
            windResult.text = ""
            cloudsResult.text = ""
            pressureResult.text = ""

            val targetColor = Color.RED
            val mp = MediaPlayer.create(this, R.raw.on_click_failure)
            val buttonAnimation = AnimationUtils.loadAnimation(this@MainActivity,
                R.anim.shrinking_animation
            )
            animationAndSound(targetColor, mp, buttonAnimation)
        }
        else {
            tempUrl = url + "?q=" + city + "&appid=" + appid

            val weather = Weather()
            val stringRequest = StringRequest(
                Request.Method.POST, tempUrl,
                { response ->

                    try {
                        val jsonResponse = JSONObject(response)
                        val jsonArray = jsonResponse.getJSONArray("weather")

                        val jsonObjectWeather = jsonArray.getJSONObject(0)

                        val description = jsonObjectWeather.getString("description")

                        val jsonObjectMain = jsonResponse.getJSONObject("main")

                        val temp = jsonObjectMain.getDouble("temp") - 273.15
                        val pressure = jsonObjectMain.getInt("pressure").toFloat()
                        val humidity = jsonObjectMain.getInt("humidity")
                        //Log.i("humidity", humidity.toString())

                        val jsonObjectWind = jsonResponse.getJSONObject("wind")

                        val wind = jsonObjectWind.getString("speed")

                        val jsonObjectClouds = jsonResponse.getJSONObject("clouds")

                        val clouds = jsonObjectClouds.getString("all")
                        val cityName = jsonResponse.getString("name")

                        var output1 = "${resources.getString(R.string.curr_wea)} $cityName"
                        var output2 = "${resources.getString(R.string.temp)} ${df.format(temp)} °C"
                        var output3 = "${resources.getString(R.string.hum)} $humidity%"
                        var output4 = "${resources.getString(R.string.desc)} $description"
                        var output5 = "${resources.getString(R.string.wind)} $wind m/s"
                        var output6 = "${resources.getString(R.string.cloud)} $clouds%"
                        var output7 = "${resources.getString(R.string.press)} $pressure hPa"

                        cityNameResult.text = output1

                        tempResult.text = output2
                        humidityResult.text = output3
                        descriptionResult.text = output4
                        windResult.text = output5
                        cloudsResult.text = output6
                        pressureResult.text = output7

                        cityNameS = cityName
                        tempS = df.format(temp)
                        humidityS = humidity.toString()
                        descriptionS = description
                        windS = wind
                        cloudsS = clouds
                        pressureS = pressure.toString()

                        val targetColor = Color.GREEN
                        var mp = MediaPlayer.create(this, R.raw.on_click_success)
                        val buttonAnimation = AnimationUtils.loadAnimation(this@MainActivity,
                            R.anim.growing_animation
                        )
                        animationAndSound(targetColor, mp, buttonAnimation)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->

                    val targetColor = Color.RED
                    val mp = MediaPlayer.create(this, R.raw.on_click_failure)
                    val buttonAnimation = AnimationUtils.loadAnimation(this@MainActivity,
                        R.anim.shrinking_animation
                    )
                    animationAndSound(targetColor, mp, buttonAnimation)
                }
            )

            var requestQueue: RequestQueue = Volley.newRequestQueue(view.context)
            requestQueue.add(stringRequest)

            val saveBtn =
                findViewById<com.google.android.material.button.MaterialButton>(R.id.save_btn)
            saveBtn.visibility = View.VISIBLE

        }
    }

    fun animationAndSound(targetColor : Int, mp : MediaPlayer, buttonAnimation : Animation) {
        val showWeatherDetailsButton = findViewById<MaterialButton>(R.id.show_weather_details)
        //val buttonAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shrinking_animation)
        buttonAnimation.reset()
        showWeatherDetailsButton.clearAnimation()
        showWeatherDetailsButton.startAnimation(buttonAnimation)

        val originalColor = showWeatherDetailsButton.backgroundTintList?.defaultColor ?: Color.TRANSPARENT

        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, targetColor)

        colorAnimator.duration = 1000 // Adjust the duration as needed
        colorAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            showWeatherDetailsButton.backgroundTintList = ColorStateList.valueOf(animatedValue)
        }
        // Create a second ValueAnimator for the transition back to the original color
        val reverseColorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), targetColor, originalColor)
        reverseColorAnimator.duration = 1000

        reverseColorAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            showWeatherDetailsButton.backgroundTintList = ColorStateList.valueOf(animatedValue)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(colorAnimator, reverseColorAnimator)
        animatorSet.start()

        mp.start()
    }

}