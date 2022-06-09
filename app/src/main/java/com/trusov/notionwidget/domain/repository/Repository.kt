package com.trusov.notionwidget.domain.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.Path

interface Repository {
    fun getPageIds(dbId: String): Observable<DbQueryDto>
    fun getPageBlocks(pageId: String): Observable<BlockResponseDto>
    fun getDatabase(dbId: String): Observable<DbDto>
}