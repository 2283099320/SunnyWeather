package com.unnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.unnyweather.android.MainActivity
import com.unnyweather.android.R
import com.unnyweather.android.ui.weather.WeatherActivity

import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment() {
    //利用lazy函数获取PlaceViewModel的实例，允许我们在整个类中随时使用viewModel变量。
    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }
    //onActivityCreated()方法给RecyclerView设置了LayoutManager和适配器，并使用了PlaceViewModel中的placeList集合作为数据源。
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(activity is MainActivity && viewModel.isPlaceSaved()){
            val place =viewModel.getSavedPlace();
            val intent= Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable: Editable? ->
            //使用addTextChangedListener()方法来监听搜索框内容的变化情况
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
                //通过searchPlaces()方法进行搜索框新内容传递
            } else {
                //入过搜索框内容为空，我们将RecyclerView隐藏
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        };
        //对placeLiveData进行实时观察，有数据变化时，回调到传入的Observer接口实现。
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                //如果数据不为空，那么就将数据添加到PlaceviewModel的placeList集合中，并让PlaceAdapter刷新界面。
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                //如果数据为空，弹出异常，并打印出具体异常。
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}