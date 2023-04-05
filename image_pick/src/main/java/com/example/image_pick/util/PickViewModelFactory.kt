package com.example.image_pick.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.image_pick.PickConfiguration
import com.example.image_pick.data.PickRepository
import com.example.image_pick.ui.PickViewModel

internal class PickViewModelFactory(
    private val pickRepository: PickRepository,
    private val pickUriManager: PickUriManager,
    private val pickConfiguration: PickConfiguration,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PickViewModel::class.java)) {
            PickViewModel(this.pickRepository, this.pickConfiguration, this.pickUriManager) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}