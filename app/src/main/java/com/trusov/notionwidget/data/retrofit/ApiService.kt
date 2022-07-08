package com.trusov.notionwidget.data.retrofit

import com.google.gson.JsonElement
import com.trusov.notionwidget.data.dto.NoteIdsDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.filter.filter_dto.FilterWrapperDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("databases/{db}/query")
    fun getPageIds(
        @Path("db") dbId: String,
        @Body filter: FilterWrapperDto
    ): Observable<NoteIdsDto>

    @GET("blocks/{pageId}/children")
    fun getPageBlocks(
        @Path("pageId") pageId: String
    ): Observable<BlockResponseDto>

/*    @GET("databases/{db}")
    fun getDatabase(
        @Path("db") dbId: String
    ): Observable<DbDto>*/

    @GET("databases/{db}")
    fun getDatabaseJson(
        @Path("db") dbId: String
    ): Observable<Response<JsonElement>>

}