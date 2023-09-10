package android.tvz.hr.finalproject.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.model.Weather
import android.tvz.hr.finalproject.view.WeatherDetailsActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.DecimalFormat

class WeatherAdapter(
    options: FirestoreRecyclerOptions<Weather>,
    private val context: Context,
    private val resources: Resources,
    private val editButtonClickListener: OnEditButtonClickListener,
    private val deleteButtonClickListener: OnDeleteButtonClickListener
) :
    FirestoreRecyclerAdapter<Weather, WeatherAdapter.WeatherViewHolder>(options){
/*class WeatherAdapter(options: FirestoreRecyclerOptions<Weather>, private val context: Context) :
    FirestoreRecyclerAdapter<Weather, WeatherAdapter.WeatherViewHolder>(options){*/

    private var isItemClickEnabled: Boolean = true

    fun setItemClickEnabled(enabled: Boolean) {
        isItemClickEnabled = enabled
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int, weather: Weather) {

        val df : DecimalFormat = DecimalFormat("#.##")
        //holder.noteTitle.setText(weather.noteTitle)
        //var isItemClickEnabled: Boolean = true

        holder.noteTitle.setText(weather.noteTitle)
        holder.noteTitle.setClickable(false)
        holder.noteTitle.setEnabled(false)
        holder.noteTitle.setFocusable(false)

        holder.cityNameResult.setText("${resources.getString(R.string.curr_wea)} ${weather.cityName}")
        holder.tempResult.setText("${resources.getString(R.string.temp)} ${weather.temp} °C")
        holder.humidityResult.setText("${resources.getString(R.string.hum)} ${weather.humidity}%")
        holder.descriptionResult.setText("${resources.getString(R.string.desc)} ${weather.description}")
        holder.windResult.setText("${resources.getString(R.string.wind)} ${weather.wind} m/s")
        holder.cloudsResult.setText("${resources.getString(R.string.cloud)} ${weather.clouds}%")
        holder.pressureResult.setText("${resources.getString(R.string.press)} ${weather.pressure} hPa")

        holder.editButton.setOnClickListener {
            editButtonClickListener.onEditButtonClick(position)
        }

        holder.deleteButton.setOnClickListener {
            deleteButtonClickListener.onDeleteButtonClick(position)
        }

        holder.itemView.setOnClickListener { v ->
            if (isItemClickEnabled) {
                if ((context as WeatherDetailsActivity).isItemClickEnabled) {
                    val intent = Intent(context, WeatherDetailsActivity::class.java)
                    intent.putExtra("noteTitle", weather.noteTitle)
                    intent.putExtra("city", weather.cityName)
                    intent.putExtra("temp", weather.temp)
                    intent.putExtra("humidity", weather.humidity)
                    intent.putExtra("description", weather.description)
                    intent.putExtra("wind", weather.wind)
                    intent.putExtra("clouds", weather.clouds)
                    intent.putExtra("pressure", weather.pressure)

                    val docId = this.snapshots.getSnapshot(position).id
                    intent.putExtra("docId", docId)

                    context.startActivity(intent)
                }
            }
        }
    }

    interface OnEditButtonClickListener {
        fun onEditButtonClick(position: Int)
    }

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_weather_item,
            parent, false)

        return WeatherViewHolder(view)
    }

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val noteTitle = itemView.findViewById<EditText>(R.id.title_text_view)

        val cityNameResult = itemView.findViewById<TextView>(R.id.city_name_text_view)
        var tempResult = itemView.findViewById<TextView>(R.id.temperature_text_view)
        var humidityResult = itemView.findViewById<TextView>(R.id.humidity_text_view)
        var descriptionResult = itemView.findViewById<TextView>(R.id.description_text_view)
        var windResult = itemView.findViewById<TextView>(R.id.wind_text_view)
        var cloudsResult = itemView.findViewById<TextView>(R.id.clouds_text_view)
        var pressureResult = itemView.findViewById<TextView>(R.id.pressure_text_view)

        var editButton = itemView.findViewById<ImageButton>(R.id.edit_button)
        var deleteButton = itemView.findViewById<ImageButton>(R.id.delete_button)

    }

}
