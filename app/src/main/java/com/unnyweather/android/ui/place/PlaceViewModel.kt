package com.unnyweather.android.ui.place

import androidx.lifecycle.*

import com.unnyweather.android.logic.Repository
import com.unnyweather.android.logic.dao.PlaceDao
import com.unnyweather.android.logic.model.Place
//定义ViewModel层
class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()
    //定义了placeList集合，用于缓存界面上显示的城市数据
    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        //运用Transformations的switchMap()方法观察searchLiveData对象。
        Repository.searchPlaces(query)
    }
    //将传入的搜索参数赋值给了searchLiveData对象。
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
    fun savePlace(place: Place)=Repository.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()


}