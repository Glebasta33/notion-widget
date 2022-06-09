package com.trusov.notionwidget.data.retrofit

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.CreatedTime
import com.trusov.notionwidget.data.dto.filter.Filter
import com.trusov.notionwidget.data.dto.filter.FilterLastWeekDto
import com.trusov.notionwidget.data.dto.filter.PastMonth
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("databases/{db}/query")
    fun getPageIds(
        @Path("db") dbId: String,
        @Body filter: FilterLastWeekDto = FilterLastWeekDto(Filter(
            timestamp = "created_time",
            created_time = CreatedTime(PastMonth())
        ))
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