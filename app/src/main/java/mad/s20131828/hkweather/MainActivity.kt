package mad.s20131828.hkweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        requestCurrentWeather()
        request7dayWeather()
    }

    //Request for Current Weather Information
    private fun requestCurrentWeather (){
        val currentDegree = findViewById<TextView>(R.id.currentDegree)
        val warnMessage = findViewById<TextView>(R.id.warnMessage)
        //Url link of HKO current weather API
        val url = "https://data.weather.gov.hk/weatherAPI/opendata/weather.php?dataType=rhrread&lang=tc"
        val queue = Volley.newRequestQueue(this)
        val currentWeatherRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                //Request for current degree display
                currentDegree.text = response.getJSONObject("temperature")
                    .getJSONArray("data")
                    //Weather of HKO HQ
                    .getJSONObject(1)
                    .getInt("value").toString()

                //Request for weather warning message display if any
                //HKO put warning message in to multiple index as array, a loop for array must be
                // done for showing correct and all messages if any, and avoid crashed if it is null
                val warnMessContainer = response.getJSONArray("warningMessage")
                val warnMessCheck = response.getString("warningMessage")
                var warnCollectend = ""
                if (warnMessCheck != "") {
                    for (warnArray in 0 until warnMessContainer.length()) {
                        warnCollectend =
                                " " + warnCollectend + " " + warnMessContainer.getString(warnArray)
                        }
                    warnMessage.text = warnCollectend
                    }else {
                    warnMessage.text = response.getString("warningMessage")
                }
            },
            Response.ErrorListener {
                    error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        )
        queue.add(currentWeatherRequest)
    }

    //Request for 7 days weather report
    private fun request7dayWeather (){
        val sevendayReport = findViewById<TextView>(R.id.sevendayReport)
        val sevendayDegree = findViewById<TextView>(R.id.sevendayDegree)
        //url link of HKO 9 days weather API
        val url = "https://data.weather.gov.hk/weatherAPI/opendata/weather.php?dataType=fnd&lang=tc"
        val queue = Volley.newRequestQueue(this)
        //val countArray = 0 //counting array start from 0
        //Date related request
        val sevendate: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                //create for loop to append all date in one piece for lesser code
                for (countArray in 0..6) {
                    //val of date
                    val date = (response.getJSONArray("weatherForecast")
                        .getJSONObject(countArray)
                        .getString("forecastDate"))

                    //val of which day of the week it is
                    val week = (response.getJSONArray("weatherForecast")
                        .getJSONObject(countArray)
                        .getString("week"))

                    sevendayReport.append(date.substring(0,4) + "年" + date.substring(4,6) + "月" + date.substring(6,8) + "日" + "  " + week + "\n\n")
                }
            },
            Response.ErrorListener {
                    error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        )
        //Weather degree of each date request
        val sevendegree: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                //create for loop to append all date in one piece for lesser code
                for (countArray in 0..6) {
                    //val of maximum temperature
                    val maxTemp = (response.getJSONArray("weatherForecast")
                        .getJSONObject(countArray)
                        .getJSONObject("forecastMaxtemp")
                        .getInt("value").toString())

                    //val of minimum temperature
                    val miniTemp = (response.getJSONArray("weatherForecast")
                        .getJSONObject(countArray)
                        .getJSONObject("forecastMintemp")
                        .getInt("value").toString())

                    sevendayDegree.append("$miniTemp°C - $maxTemp°C\n\n")
                }
            },
            Response.ErrorListener {
                    error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        )
        queue.add(sevendate)
        queue.add(sevendegree)
    }
}
