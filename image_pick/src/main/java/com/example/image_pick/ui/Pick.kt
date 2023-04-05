package com.example.image_pick.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.image_pick.PickConfiguration
import com.example.image_pick.R
import com.example.image_pick.data.PickImage
import com.example.image_pick.data.PickRepositoryImpl
import com.example.image_pick.theme.PickDimens
import com.example.image_pick.theme.PickDimens.HalfQuarter
import com.example.image_pick.util.PickUriManager
import com.example.image_pick.util.PickViewModelFactory
import kotlinx.coroutines.flow.StateFlow

private const val Select = "SELECT"
private const val One = 1
private const val Three = 3

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Pick(
    modifier: Modifier = Modifier,
    pickConfiguration: PickConfiguration = PickConfiguration(),
    onPhotoSelected: (MutableList<PickImage>) -> Unit,
) {
    val context = LocalContext.current
    val gridState: LazyGridState = rememberLazyGridState()

    val pickViewModel: PickViewModel = viewModel(
        factory = PickViewModelFactory(
            PickRepositoryImpl(
                context,
            ),
            PickUriManager(context),
            pickConfiguration
        )
    )

    val lazyPickImages: LazyPagingItems<PickImage> =
        pickViewModel.getImages().collectAsLazyPagingItems()

    Scaffold(floatingActionButton = {

        ExtendedFloatingActionButton(
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            text = { Text(text = "Выбрать") },
            onClick = { onPhotoSelected(pickViewModel.selectedImage.value as MutableList<PickImage>) },
            icon = { Icon(Icons.Rounded.Check, "fab-icon") }
        )
    }, content = {
        val newModifier = modifier.padding(HalfQuarter)
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { it ->Boolean
                onPhotoSelected((listOf(pickViewModel.getPickImage()) as List<PickImage>).toMutableList())
                lazyPickImages.refresh()
            }

        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            columns = GridCells.Fixed(Three)
        ) {
            item {
                CameraIcon(
                    modifier = newModifier,
                    cameraLauncher = cameraLauncher,
                    pickViewModel = pickViewModel
                )
            }
            items(lazyPickImages.itemCount) { index ->
                lazyPickImages[index]?.let { pickImage ->
                    PickImage(
                        modifier = newModifier,
                        pickImage = pickImage,
                        pickConfiguration = pickConfiguration,
                        selectedImages = pickViewModel.selectedImage,
                        onSelectedPhoto = { image, isSelected ->
                            pickViewModel.isPhotoSelected(
                                pickImage = image,
                                isSelected = isSelected
                            )
                        }
                    )
                }
            }
        }
    })
}

@Composable
internal fun CameraIcon(
    modifier: Modifier,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    pickViewModel: PickViewModel,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(PickDimens.Sixteen)
            .clickable { handleCamera(pickViewModel, cameraLauncher) }
            .then(Modifier.background(MaterialTheme.colorScheme.background))
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.ic_camera),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(PickDimens.Six)
                .alpha(0.2F)
        )
    }
}

private fun handleCamera(
    pickViewModel: PickViewModel,
    onPhotoClicked: ManagedActivityResultLauncher<Uri, Boolean>,
) {
    onPhotoClicked.launch(pickViewModel.getCameraImageUri())
}
@Composable
internal fun PickImage(
    modifier: Modifier,
    pickImage: PickImage,
    selectedImages: StateFlow<List<PickImage>>,
    pickConfiguration: PickConfiguration,
    onSelectedPhoto: (PickImage, isSelected: Boolean) -> Unit,
) {

    val selected = remember { mutableStateOf(false) }
    val images by selectedImages.collectAsState(initial = emptyList())
    val backgroundColor = if (selected.value) Color.Black else Color.Transparent

    Box(
        modifier = modifier
            .size(PickDimens.Sixteen),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberAsyncImagePainter(pickImage.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )

        Box(
            modifier = modifier
                .clickable {
                    if (!pickConfiguration.multipleImagesAllowed) {
                        if (images.isEmpty()) {
                            selected.value = !selected.value
                            onSelectedPhoto(pickImage, selected.value)
                        } else {
                            selected.value = false
                            onSelectedPhoto(pickImage, selected.value)
                        }
                    } else {
                        selected.value = !selected.value
                        onSelectedPhoto(pickImage, selected.value)
                    }
                }
                .fillMaxSize()
                .alpha(0.5F)
                .background(color = backgroundColor),
        ) {
            PickImageIndicator(
                modifier = modifier,
                text = images.indexOf(pickImage).plus(One).toString()
            )
        }
    }
}

@Composable
internal fun PickImageIndicator(modifier: Modifier = Modifier, text: String) {
    if (text.toInt() > 0) {
        val textColor = MaterialTheme.colorScheme.onPrimary

        Text(
            text = text,
            textAlign = TextAlign.End,
            color = textColor,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = modifier
                .fillMaxSize()
                .padding(PickDimens.One)
        )
    }
}