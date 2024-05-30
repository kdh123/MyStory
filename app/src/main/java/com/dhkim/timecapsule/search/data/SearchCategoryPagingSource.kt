package com.dhkim.timecapsule.search.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.domain.Place
import retrofit2.HttpException
import java.io.IOException

class SearchCategoryPagingSource(
    private val api: SearchApi,
    private val category: Category,
    private val lat: String,
    private val lng: String
) : PagingSource<Int, Place>() {
    private var isEnd = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
        try {
            val nextPageNumber = params.key ?: 1
            val result = api.getPlaceByCategory(
                category = category.code,
                lat = lat,
                lng = lng,
                page = nextPageNumber,
            )

            val places = if (result.isSuccessful) {
                if (!isEnd) {
                    result.body()?.run {
                        isEnd = meta.is_end
                        documents.map { it.toPlace() }
                    } ?: listOf()
                } else {
                    listOf()
                }
            } else {
                listOf()
            }

            return LoadResult.Page(
                data = places,
                prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1,
                nextKey = if (category.code.isNotEmpty()) {
                    nextPageNumber + 1
                } else null
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}