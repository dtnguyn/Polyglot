package com.moderndev.polyglot.ui

import android.app.Activity
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.moderndev.polyglot.R
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.moderndev.polyglot.model.AuthStatus
import com.moderndev.polyglot.model.Token
import com.moderndev.polyglot.ui.navigation.PolyglotScreens
import com.moderndev.polyglot.ui.screens.*
import com.moderndev.polyglot.ui.screens.account.AccountScreen
import com.moderndev.polyglot.ui.screens.account.AccountViewModel
import com.moderndev.polyglot.ui.screens.auth.AuthViewModel
import com.moderndev.polyglot.ui.screens.auth.ChangePasswordScreen
import com.moderndev.polyglot.ui.screens.auth.VerifyCodeScreen
import com.moderndev.polyglot.ui.screens.definition.WordDetailViewModel
import com.moderndev.polyglot.ui.screens.newsDetail.NewsDetailViewModel
import com.moderndev.polyglot.ui.screens.newsDetail.NewsDetailScreen
import com.moderndev.polyglot.ui.screens.feeds.FeedViewModel
import com.moderndev.polyglot.ui.screens.home.HomeViewModel
import com.moderndev.polyglot.ui.screens.newsDetail.VideoDetailScreen
import com.moderndev.polyglot.ui.screens.search.SearchViewModel
import com.moderndev.polyglot.ui.screens.setting.SettingScreen
import com.moderndev.polyglot.ui.screens.stats.StatsScreen
import com.moderndev.polyglot.ui.screens.stats.StatsViewModel
import com.moderndev.polyglot.ui.screens.videoDetail.VideoDetailViewModel
import com.moderndev.polyglot.ui.screens.wordReview.WordReviewResultScreen
import com.moderndev.polyglot.ui.screens.wordReview.WordReviewScreen
import com.moderndev.polyglot.ui.screens.wordReview.WordReviewViewModel
import com.moderndev.polyglot.ui.theme.PawnTheme
import com.moderndev.polyglot.util.DataStoreUtils
import com.moderndev.polyglot.util.UIState


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun PolygotApp(
    activity: Activity,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    wordDetailViewModel: WordDetailViewModel,
    searchViewModel: SearchViewModel,
    sharedViewModel: SharedViewModel,
    feedViewModel: FeedViewModel,
    newsDetailViewModel: NewsDetailViewModel,
    videoDetailViewModel: VideoDetailViewModel,
    wordReviewViewModel: WordReviewViewModel,
    statsViewModel: StatsViewModel,
    accountViewModel: AccountViewModel
) {

    val navController = rememberNavController()
    val items = arrayListOf(
        PolyglotScreens.Home,
        PolyglotScreens.Search,
//        PolyglotScreens.Feeds,
//        PolyglotScreens.Stats
    )
    val authStatusUIState: UIState<AuthStatus> by sharedViewModel.authStatusUIState

    val context = LocalContext.current
    LaunchedEffect(true) {
        Log.d("PawnApp", "pawn user ${DataStoreUtils.getUserFromDataStore(context)}")
        sharedViewModel.initializeAuthStatus(
            AuthStatus(
                user = DataStoreUtils.getUserFromDataStore(context),
                token = Token(
                    DataStoreUtils.getAccessTokenFromDataStore(context),
                    DataStoreUtils.getRefreshTokenFromDataStore(context)
                )
            )
        )
    }

    LaunchedEffect(authStatusUIState) {
        when (authStatusUIState) {
            is UIState.Initial -> {

            }
            is UIState.Error -> {

            }
            is UIState.Loading -> {

            }
            is UIState.Loaded -> {
                if(authStatusUIState.value?.user != null){
                    items.add(PolyglotScreens.Feeds)
                    items.add(PolyglotScreens.Stats)
                }
            }
        }
    }

    PawnTheme {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                if (currentRoute in setOf("home", "feeds", "search", "stats")) {
                    BottomNavigation(Modifier.height(55.dp)) {
                        items.forEach { screen ->
                            BottomNavigationItem(
                                icon = {
                                    Icon(
                                        painterResource(id = screen.icon ?: R.drawable.home), null,
                                        Modifier
                                            .width(28.dp)
                                            .height(28.dp)
                                    )
                                },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        navController.graph.startDestinationRoute?.let {
                                            popUpTo(it) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) {

            NavHost(navController, startDestination = PolyglotScreens.Home.route) {
                composable(PolyglotScreens.Home.route) {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        sharedViewModel = sharedViewModel,
                        navController = navController
                    )

                }
                composable(PolyglotScreens.Feeds.route) {
                    FeedScreen(
                        sharedViewModel = sharedViewModel,
                        feedViewModel = feedViewModel,
                        navController = navController
                    )
                }
                composable(PolyglotScreens.Search.route) {
                    SearchScreen(
                        searchViewModel,
                        sharedViewModel,
                        navController
                    )
                }
                composable("${PolyglotScreens.WordDetail.route}/{wordValue}/{language}") {
                    WordDetailScreen(
                        navController = navController,
                        viewModel = wordDetailViewModel,
                        sharedViewModel = sharedViewModel,
                        wordValue = it.arguments?.getString("wordValue"),
                        language = it.arguments?.getString("language")
                    )
                }
                composable(PolyglotScreens.Auth.route) {
                    AuthScreen(
                        authViewModel = authViewModel,
                        sharedViewModel = sharedViewModel,
                        navController = navController
                    )
                }
                composable(PolyglotScreens.ChangePassword.route) {
                    ChangePasswordScreen(
                        navController = navController,
                        viewModel = authViewModel
                    )
                }
                composable(PolyglotScreens.VerifyCode.route) { VerifyCodeScreen(navController = navController, viewModel = authViewModel) }
                composable(
                    "${PolyglotScreens.NewsDetail.route}/{feedId}/{feedUrl}",
                    arguments = listOf()
                ) {
                    NewsDetailScreen(
                        viewModel = newsDetailViewModel,
                        sharedViewModel = sharedViewModel,
                        navController = navController,
                        newsUrl = it.arguments?.getString("feedUrl") ?: "",
                        newsId = (it.arguments?.getString("feedId")) ?: "",
//                        title = if(it.arguments?.getString("title") == "null") null else it.arguments?.getString("title"),
//                        publishedDate = if(it.arguments?.getString("publishedDate") == "null") null else it.arguments?.getString("publishedDate"),
//                        thumbnail = if(it.arguments?.getString("thumbnail") == "null") null else it.arguments?.getString("thumbnail"),
                    )
                }

                composable("${PolyglotScreens.VideoDetail.route}/{videoId}") {
                    VideoDetailScreen(
                        viewModel = videoDetailViewModel,
                        sharedViewModel = sharedViewModel,
                        navController = navController,
                        videoId = it.arguments?.getString("videoId") ?: ""
                    )
                }
                composable(PolyglotScreens.WordReviewMenu.route) {
                    WordReviewMenuScreen(
                        navController = navController
                    )
                }
                composable(PolyglotScreens.WordReview.route) {
                    WordReviewScreen(
                        navController = navController,
                        viewModel = wordReviewViewModel,
                        sharedViewModel = sharedViewModel
                    )
                }
                composable(PolyglotScreens.WordReviewResult.route) {
                    WordReviewResultScreen(
                        navController = navController,
                        viewModel = wordReviewViewModel
                    )
                }

                composable(PolyglotScreens.Stats.route) {
                   StatsScreen(navController = navController, sharedViewModel = sharedViewModel, viewModel = statsViewModel)

                }

                composable(PolyglotScreens.Account.route) {
                    AccountScreen(navController = navController, sharedViewModel = sharedViewModel, viewModel = accountViewModel)
                }
                composable(PolyglotScreens.Setting.route) {
                    SettingScreen(activity = activity, navController = navController, sharedViewModel = sharedViewModel)
                }
            }
        }
    }
}



