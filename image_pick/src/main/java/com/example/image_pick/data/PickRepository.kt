package com.example.image_pick.data

import android.content.Context
import androidx.paging.PagingSource
import com.example.image_pick.util.createCursor
import com.example.image_pick.util.fetchPagePicture

private const val Zero = 0
private const val First = 1

internal interface PickRepository {
    suspend fun getCount(): Int
    suspend fun getByOffset(offset: Int): PickImage?
    fun getPicturePagingSource(): PagingSource<Int, PickImage>
}
internal class PickRepositoryImpl(private val context: Context) : PickRepository {

    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, Zero) ?: return Zero
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): PickImage? {
        return context.fetchPagePicture(First, offset).firstOrNull()
    }

    override fun getPicturePagingSource(): PagingSource<Int, PickImage> {
        return PickDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }
}