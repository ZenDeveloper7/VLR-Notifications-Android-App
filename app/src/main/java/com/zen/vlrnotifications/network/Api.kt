package com.zen.vlrnotifications.network

import com.zen.vlrnotifications.models.ValorantMatchModel
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("get-vlr-matches")
    suspend fun getVlrMatches(@Query("page") page: Int): List<ValorantMatchModel>
}