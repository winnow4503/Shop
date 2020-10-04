package com.jin.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.row_bus.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

class BusActivity : AppCompatActivity(), AnkoLogger {
    var bus : BusData? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)
        doAsync {
            val busService = retrofit.create(BusService::class.java)
            bus = busService.BusList()
                .execute()
                .body()
            bus?.datas?.forEach {
                info("${it.BusID} ${it.RouteID} ${it.Speed}")
            }

            uiThread {
                bus_recycler.layoutManager = LinearLayoutManager(this@BusActivity)
                bus_recycler.setHasFixedSize(true)
                bus_recycler.adapter = BusAdapter()

            }
        }


    }

    inner class BusAdapter() : RecyclerView.Adapter<BusHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            return BusHolder(view)
        }

        override fun onBindViewHolder(holder: BusHolder, position: Int) {
            val bus = bus?.datas?.get(position)
            holder.bindBus(bus!!)
        }

        override fun getItemCount(): Int {
            val size = bus?.datas?.size?: 0
            return size
        }

    }

    inner class BusHolder(view : View) : RecyclerView.ViewHolder(view) {
        val idText = view.busId
        val routeText = view.routleId
        val speedText = view.speed

        fun bindBus(budt: Data) {
            idText.text = budt.BusID
            routeText.text = budt.RouteID
            speedText.text = budt.Speed
        }
    }
}



/*
{
    "datas" : [ {
    "BusID" : "FAD-596",
    "ProviderID" : "12",
    "DutyStatus" : "0",
    "BusStatus" : "0",
    "RouteID" : "65434",
    "GoBack" : "2",
    "Longitude" : "121.221946666667",
    "Latitude" : "24.9552666666667",
    "Speed" : "0",
    "Azimuth" : "0",
    "DataTime" : "2020-10-02 14:22:13",
    "ledstate" : "0",
    "sections" : "1"
    } ]
}
*/

data class BusData(
    val datas: List<Data>
)

data class Data(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService {
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun BusList() : Call<BusData>
}
