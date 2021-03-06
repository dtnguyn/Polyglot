package com.moderndev.polyglot.ui.screens.account

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moderndev.polyglot.R
import com.moderndev.polyglot.ui.SharedViewModel
import com.moderndev.polyglot.ui.components.account.UpdateDialog
import com.moderndev.polyglot.ui.theme.TextFieldGrey
import com.moderndev.polyglot.ui.theme.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.moderndev.polyglot.ui.components.CircularLoadingBar
import com.moderndev.polyglot.ui.components.account.AvatarBottomSheetContent
import com.moderndev.polyglot.ui.components.auth.LanguageBottomSheetContent
import com.moderndev.polyglot.util.Constants
import com.moderndev.polyglot.util.DataStoreUtils
import com.moderndev.polyglot.util.UIState
import kotlinx.coroutines.launch

data class UpdateInfo(
    val label: String,
    val currentValue: String,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel,
    sharedViewModel: SharedViewModel
) {

    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }

    val authStatusUIState by sharedViewModel.authStatusUIState
    var user by remember { mutableStateOf(authStatusUIState.value?.user) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var bottomSheetMenu by remember { mutableStateOf("avatar") }
    val resources = LocalContext.current.resources
    var currentPickedAvatar by remember { mutableStateOf(user?.avatar ?: "human1") }
    var loading by remember { mutableStateOf(false) }


    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed,
        )
    )

    suspend fun updateUsername(newUsername: String) {
        Log.d("AccountScreen", "Debug Account Screen 1")
        if (authStatusUIState.value == null) return
        if (user == null) return
        Log.d("AccountScreen", "Debug Account Screen 2")

        sharedViewModel.updateUser(
            accessToken = DataStoreUtils.getAccessTokenFromDataStore(context),
            currentAuthStatus = authStatusUIState.value!!,
            username = newUsername,
            email = user!!.email,
            avatar = user!!.avatar,
            isPremium = user!!.isPremium,
            dailyWordCount = user!!.dailyWordCount,
            notificationEnabled = user!!.notificationEnabled,
            nativeLanguageId = user!!.nativeLanguageId,
            appLanguageId = user!!.appLanguageId,
            dailyWordTopic = user!!.dailyWordTopic,
            feedTopics = user!!.feedTopics
        )
    }

    suspend fun updateEmail(newEmail: String) {
        if (authStatusUIState.value == null) return
        if (user == null) return

        sharedViewModel.updateUser(
            accessToken = DataStoreUtils.getAccessTokenFromDataStore(context),
            currentAuthStatus = authStatusUIState.value!!,
            username = user!!.username,
            email = newEmail,
            avatar = user!!.avatar,
            isPremium = user!!.isPremium,
            dailyWordCount = user!!.dailyWordCount,
            notificationEnabled = user!!.notificationEnabled,
            nativeLanguageId = user!!.nativeLanguageId,
            appLanguageId = user!!.appLanguageId,
            dailyWordTopic = user!!.dailyWordTopic,
            feedTopics = user!!.feedTopics
        )
    }

    suspend fun updateNativeLanguage(newNativeLanguageId: String) {
        if (authStatusUIState.value == null) return
        if (user == null) return

        sharedViewModel.updateUser(
            accessToken = DataStoreUtils.getAccessTokenFromDataStore(context),
            currentAuthStatus = authStatusUIState.value!!,
            username = user!!.username,
            email = user!!.email,
            isPremium = user!!.isPremium,
            avatar = user!!.avatar,
            dailyWordCount = user!!.dailyWordCount,
            notificationEnabled = user!!.notificationEnabled,
            nativeLanguageId = newNativeLanguageId,
            appLanguageId = user!!.appLanguageId,
            dailyWordTopic = user!!.dailyWordTopic,
            feedTopics = user!!.feedTopics
        )
    }

    suspend fun updateAvatar(newAvatar: String?) {
        if (authStatusUIState.value == null) return
        if (user == null) return

        sharedViewModel.updateUser(
            accessToken = DataStoreUtils.getAccessTokenFromDataStore(context),
            currentAuthStatus = authStatusUIState.value!!,
            username = user!!.username,
            email = user!!.email,
            avatar = newAvatar,
            isPremium = user!!.isPremium,
            dailyWordCount = user!!.dailyWordCount,
            notificationEnabled = user!!.notificationEnabled,
            nativeLanguageId = user!!.nativeLanguageId,
            appLanguageId = user!!.appLanguageId,
            dailyWordTopic = user!!.dailyWordTopic,
            feedTopics = user!!.feedTopics
        )
    }

    fun toggleBottomSheet() {
        coroutineScope.launch {
            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                bottomSheetScaffoldState.bottomSheetState.expand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        }
    }

    LaunchedEffect(authStatusUIState) {
        when (authStatusUIState) {
            is UIState.Initial -> {

            }

            is UIState.Error -> {
                loading = false
                Toast.makeText(context, authStatusUIState.errorMsg, Toast.LENGTH_SHORT).show()
            }
            is UIState.Loading -> {
                loading = true
                Log.d("AccountScreen", "authStatus loading ${authStatusUIState.value}")

            }
            is UIState.Loaded -> {
                loading = false
                Log.d("AccountScreen", "authStatus Loaded ${authStatusUIState.value}")

                authStatusUIState.value?.user?.let {
                    user = it

                    // Store the state to DataStore
                    DataStoreUtils.saveTokenToDataStore(context, authStatusUIState.value!!.token)
                    DataStoreUtils.saveUserToDataStore(context, it)
                }
            }
        }
    }

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            currentPickedAvatar = user?.avatar ?: "human1"
        }
    }


    BottomSheetScaffold(
        backgroundColor = Color.White,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (bottomSheetMenu == "avatar") {
                AvatarBottomSheetContent(
                    currentAvatar = currentPickedAvatar,
                    onPickAvatar = {
                        currentPickedAvatar = it
                    },
                    onSave = {
                        coroutineScope.launch {
                            updateAvatar(currentPickedAvatar)
                        }
                        toggleBottomSheet()
                    }
                )
            } else {
                LanguageBottomSheetContent(
                    languages = Constants.allLanguages,
                    onLanguageClick = { language ->
                        coroutineScope.launch {
                            updateNativeLanguage(language)
                        }
                        toggleBottomSheet()
                    }
                )
            }
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    content = {
                        Image(
                            painter = painterResource(id = R.drawable.back_32_black),
                            contentDescription = "Back button"
                        )
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(id = R.string.settings),
                    style = Typography.h4,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(text = stringResource(id = R.string.logout),
                    style = Typography.body1,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            coroutineScope.launch {
                                val refreshToken =
                                    DataStoreUtils.getRefreshTokenFromDataStore(context)
                                DataStoreUtils.saveAccessTokenToAuthDataStore(context, null)
                                DataStoreUtils.saveRefreshTokenToAuthDataStore(context, null)
                                sharedViewModel.logout(refreshToken)
                                navController.popBackStack("home", false)
                            }
                        })
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Image(
                painter = painterResource(
                    resources.getIdentifier(
                        user?.avatar ?: "human1",
                        "drawable",
                        "com.moderndev.polyglot"
                    )
                ),
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable(onClick = {
                        bottomSheetMenu = "avatar"
                        toggleBottomSheet()
                    })
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = stringResource(id = R.string.change_profile_icon),
                style = Typography.body1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Username",
                style = Typography.body1,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        updateInfo = UpdateInfo("Username", "${user?.username}")
                    },
                backgroundColor = TextFieldGrey
            ) {

                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(15.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${user?.username}",
                        style = Typography.body1,
                        color = Color.Black
                    )
                }

            }
            Spacer(modifier = Modifier.padding(7.dp))
            Text(
                text = "Email",
                style = Typography.body1,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { updateInfo = UpdateInfo("Email", "${user?.email}") },
                backgroundColor = TextFieldGrey
            ) {

                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(15.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${user?.email}",
                        style = Typography.body1,
                        color = Color.Black
                    )
                }

            }
            Spacer(modifier = Modifier.padding(7.dp))
            Text(
                text = stringResource(id = R.string.native_language),
                style = Typography.body1,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        bottomSheetMenu = "language"
                        toggleBottomSheet()
                    },
                backgroundColor = TextFieldGrey
            ) {

                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(15.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${user?.nativeLanguageId}",
                        style = Typography.body1,
                        color = Color.Black
                    )
                }

            }
        }
        updateInfo?.let { info ->
            UpdateDialog(
                title = stringResource(id = R.string.update_account),
                content = info.label,
                currentValue = info.currentValue,
                actionButtonText = stringResource(id = R.string.update),
                onDismiss = {
                    updateInfo = null
                },
                placeholder = stringResource(id = R.string.update_placholder),
                onAction = {
                    coroutineScope.launch {
                        when (info.label) {
                            "Username" -> updateUsername(it)
                            "Email" -> updateEmail(it)
                            else -> updateNativeLanguage(it)
                        }
                        updateInfo = null
                    }

                }
            )
        }
    }

    if(loading){
        CircularLoadingBar()
    }


}