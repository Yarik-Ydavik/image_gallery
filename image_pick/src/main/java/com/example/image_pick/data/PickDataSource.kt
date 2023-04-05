package com.example.image_pick.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

private const val Zero = 0
private const val First = 1

class PickDataSource(private val onFetch: (limit: Int, offset: Int) -> List<PickImage>) :
    PagingSource<Int, PickImage>() {

    override fun getRefreshKey(state: PagingState<Int, PickImage>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(First)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(First)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PickImage> {
        val pageNumber = params.key ?: Zero
        val pageSize = params.loadSize
        val pictures = onFetch.invoke(pageSize, pageNumber * pageSize)
        val prevKey = if (pageNumber > Zero) pageNumber.minus(First) else null
        val nextKey = if (pictures.isNotEmpty()) pageNumber.plus(First) else null

        return LoadResult.Page(
            data = pictures,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}