package com.example.image_pick.ui.permission

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.image_pick.R
import com.example.image_pick.theme.PickDimens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Permission(
    permissions: List<String>,
    goToAppSettings: () -> Unit,
    appContent: @Composable () -> Unit,
){
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    val isPermissionDenied = remember{ mutableStateOf(false) }

    permissionState.permissions.forEach { it ->
        if (it.status.isGranted) appContent()
        else {
            if (it.status.shouldShowRationale){
                isPermissionDenied.value = true
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PickDimens.Six),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.ic_sad),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(PickDimens.Sixteen)
                            .aspectRatio(1F)
                    )
                    Spacer(modifier = Modifier.height(PickDimens.Three))

                    Text(
                        stringResource(R.string.permissions_rationale), textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                    )
                    Spacer(modifier = Modifier.height(PickDimens.Three))

                    Button(
                        onClick = { goToAppSettings() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            text = "Перейти к настройкам",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            else{
                Scaffold(
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize()
                        .padding(PickDimens.One),
                ) {
                    isPermissionDenied.value = false
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(PickDimens.Six),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.ic_camera_moments),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1F)
                        )
                        Spacer(modifier = Modifier.height(PickDimens.Three))

                        Text(
                            stringResource(R.string.permission_prompt), textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                        )
                        Spacer(modifier = Modifier.height(PickDimens.Three))


                        Button(
                            onClick = {
                                permissionState.launchMultiplePermissionRequest()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary),
                        ) {
                            Text(
                                text = "Предоставить доступ",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}