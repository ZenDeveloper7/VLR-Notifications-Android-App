package com.zen.vlrnotifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.vlrnotifications.models.ValorantMatchModel
import com.zen.vlrnotifications.network.Resource
import com.zen.vlrnotifications.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _vlrMatchesMutableStatFlow =
        MutableStateFlow<Resource<List<ValorantMatchModel>>>(Resource.idle())
    val vlrMatchesMutableStatFlow = _vlrMatchesMutableStatFlow

    fun getMatches(page: Int) {
        viewModelScope.launch {
            _vlrMatchesMutableStatFlow.value = Resource.loading()
            try {
                val response = RetrofitClient.getApi().getVlrMatches(page)
                _vlrMatchesMutableStatFlow.value = Resource.success(response)
            } catch (e: Exception) {
                _vlrMatchesMutableStatFlow.value = Resource.error(e.message ?: "An error occurred")
            }
        }
    }
}