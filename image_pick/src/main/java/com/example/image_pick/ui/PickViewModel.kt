package com.example.image_pick.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.image_pick.PickConfiguration
import com.example.image_pick.data.PickImage
import com.example.image_pick.data.PickRepository
import com.example.image_pick.util.PickUriManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PickViewModel(
    private val pickRepository: PickRepository,
    private val pickConfiguration: PickConfiguration,
    private val pickUriManager: PickUriManager,
): ViewModel() {
    private val selectedImageList: MutableList<PickImage> = ArrayList()
    private val _selectedImage = MutableStateFlow(emptyList<PickImage>())
    private var uri: Uri? = null

    val selectedImage: StateFlow<List<PickImage>> = _selectedImage

    fun getPickImage() = pickUriManager.getPickImage(uri)

    fun getImages(): Flow<PagingData<PickImage>> = Pager(
        config = PagingConfig(pageSize = 50, initialLoadSize = 50, enablePlaceholders = true)
    ) {
        pickRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)

    fun isPhotoSelected(pickImage: PickImage, isSelected: Boolean) {
        if (isSelected) {
            if (pickConfiguration.multipleImagesAllowed) {
                selectedImageList.add(pickImage)
            } else {
                if (selectedImageList.isEmpty() && selectedImageList.count() < 1) {
                    selectedImageList.add(pickImage)
                }
            }
        } else {
            selectedImageList.filter { it.id == pickImage.id }
                .forEach { selectedImageList.remove(it) }
        }
        _selectedImage.value = (selectedImageList).toSet().toList()
    }

    fun getCameraImageUri(): Uri? {
        uri = pickUriManager.getNewUri()
        return uri
    }
}