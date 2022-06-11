package com.trusov.notionwidget.data.retrofit

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("databases/{db}/query")
    fun getPageIds(
        @Path("db") dbId: String,
        @Body filter: FilterWrapperDto = FilterWrapperDto(
            Filter(
                multi_select = MultiSelect("Задачи"),
                property = "Topic"
            )
        )
    ): Observable<DbQueryDto>

    @GET("blocks/{pageId}/children")
    fun getPageBlocks(
        @Path("pageId") pageId: String
    ): Observable<BlockResponseDto>

    @GET("databases/{db}")
    fun getDatabase(
        @Path("db") dbId: String
    ): Observable<DbDto>

}