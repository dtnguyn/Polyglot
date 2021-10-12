package com.nguyen.polyglot.ui.screens.newsDetail

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.nguyen.polyglot.R
import com.nguyen.polyglot.model.FeedDetail
import com.nguyen.polyglot.model.NewsDetail
import com.nguyen.polyglot.model.Word
import com.nguyen.polyglot.ui.SharedViewModel
import com.nguyen.polyglot.ui.components.BackHandler
import com.nguyen.polyglot.ui.components.SelectableText
import com.nguyen.polyglot.ui.components.feedDetail.news.LoadingNews
import com.nguyen.polyglot.ui.components.feedDetail.news.WordActionMenu
import com.nguyen.polyglot.ui.components.feedDetail.news.WordDefinition
import com.nguyen.polyglot.ui.navigation.PolyglotScreens
import com.nguyen.polyglot.ui.theme.Typography
import com.nguyen.polyglot.util.DataStoreUtils
import com.nguyen.polyglot.util.UIState
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalPagerApi::class)
@Composable
fun NewsDetailScreen(
    viewModel: NewsDetailViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    title: String,
    publishedDate: String?,
    thumbnail: String?,
    newsId: String,
    newsUrl: String
) {


    val newsDetailUIState: UIState<FeedDetail<NewsDetail>> by viewModel.newsDetailUIState
    var newsDetail by remember { mutableStateOf(newsDetailUIState.value) }
    val wordDefinitionUIState: UIState<Word> by viewModel.wordDefinitionUIState
    var wordDefinition by remember { mutableStateOf(wordDefinitionUIState.value) }
    var currentFocusWord: String? by remember { mutableStateOf(wordDefinition?.value) }
    var loading by remember { mutableStateOf(false) }
    val focusMode by viewModel.focusMode
    val isFindingDefinition by viewModel.isFindingDefinition

    val coroutineScope = rememberCoroutineScope()
    val selectableTextRange = remember { mutableStateOf<TextRange?>(null) }
    val articleScrollState = rememberScrollState()
    val context = LocalContext.current
    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )



    LaunchedEffect(true) {
        if (newsDetailUIState !is UIState.Loaded) {
            viewModel.getNewsDetail(
                DataStoreUtils.getAccessTokenFromDataStore(context),
                newsId,
                newsUrl.replace("<", "/")
            )
        }

    }

    LaunchedEffect(newsDetailUIState) {
        when (newsDetailUIState) {
            is UIState.Initial -> {

            }
            is UIState.Loading -> {
                loading = true

            }
            is UIState.Error -> {
                loading = false

            }
            is UIState.Loaded -> {
                loading = false
                newsDetail = newsDetailUIState.value
            }


        }
    }
    LaunchedEffect(bottomSheetScaffoldState.isVisible) {
        if (!bottomSheetScaffoldState.isVisible) {
            //When the bottom sheet is closed
            viewModel.setFocusMode(false)
            viewModel.setIsFindingDefinition(false)
        }
    }


    LaunchedEffect(wordDefinitionUIState) {
        when (wordDefinitionUIState) {
            is UIState.Initial -> {

            }
            is UIState.Loading -> {

            }
            is UIState.Error -> {

            }
            is UIState.Loaded -> {
                wordDefinition = wordDefinitionUIState.value
            }

        }
    }

    ModalBottomSheetLayout(

        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            if (isFindingDefinition) {
                WordDefinition(
                    word = wordDefinition,
                    isLoading = wordDefinitionUIState is UIState.Loading,
                    onBackClick = {
                        viewModel.setIsFindingDefinition(false)
                    },
                    onDetailClick = {
                        navController.navigate("${PolyglotScreens.WordDetail.route}/${wordDefinition!!.value}/${sharedViewModel.currentPickedLanguage.value?.id}")
                    }
                )
            } else {
                WordActionMenu(
                    word = currentFocusWord ?: "",
                    onLookUpDefinition = { word ->
                        coroutineScope.launch {
                            viewModel.getWordDefinition(
                                DataStoreUtils.getAccessTokenFromDataStore(
                                    context
                                ),
                                word,
                                sharedViewModel.currentPickedLanguage.value?.id
                            )
                            viewModel.setIsFindingDefinition(true)

                        }
                    },
                    onLookUpImages = { word ->
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data =
                            Uri.parse("http://images.google.com/images?um=1&hl=en&safe=active&nfpr=1&q=${word}")
                        startActivity(context, openURL, null)

                    }
                )
            }

        },
        sheetState = bottomSheetScaffoldState,
    ) {
        BackHandler(onBack = {
            viewModel.resetState()
            navController.popBackStack()
        })
        Scaffold(
            backgroundColor = Color.White, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .verticalScroll(articleScrollState),

                ) {
                coroutineScope.launch {
                    articleScrollState.scrollTo(viewModel.articleScrollPosition)
                }

                Spacer(modifier = Modifier.padding(5.dp))

                IconButton(
                    onClick = {
                        viewModel.resetState()
                        navController.popBackStack()
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back_32_black),
                        contentDescription = "Back icon"
                    )

                }

                Text(text = title, style = Typography.h3)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(text = publishedDate?.substring(0, 10) ?: "", style = Typography.subtitle2)
                Log.d("NewsDetailScreen", "url: ${newsUrl}")

                Spacer(modifier = Modifier.padding(5.dp))

                GlideImage(
                    imageModel = thumbnail?.replace("<", "/") ?: "",
                    contentScale = ContentScale.FillWidth,
                    circularReveal = CircularReveal(duration = 250),
                    placeHolder = ImageBitmap.imageResource(id = R.drawable.cat_loading_icon),
                    error = ImageBitmap.imageResource(R.drawable.image_loading_error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                )

                Spacer(modifier = Modifier.padding(10.dp))

                if (loading) {
                    LoadingNews()
                } else {
                    newsDetail?.content?.value?.let {
                        SelectableText(
                            text = it,
                            textRange = selectableTextRange,
                            isFocusing = focusMode,
                            onLongClick = { word ->
                                if (word != "") {
                                    Log.d(
                                        "NewsDetailScreen",
                                        "scroll position: ${articleScrollState.value}"
                                    )
                                    viewModel.updateArticleScrollPosition(articleScrollState.value)
                                    viewModel.setFocusMode(true)
                                    currentFocusWord = word
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

}

