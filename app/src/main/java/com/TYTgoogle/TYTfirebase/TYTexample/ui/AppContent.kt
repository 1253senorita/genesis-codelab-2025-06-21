package com.TYTgoogle.TYTfirebase.TYTexample.ui


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.TYTgoogle.TYTfirebase.TYTexample.MainViewModel // MainViewModel 경로 확인!
import com.TYTgoogle.TYTfirebase.TYTexample.ui.theme.FirebaseDataConnectTheme
import com.TYTgoogle.TYTfirebase.TYTexample.ui.LoginRoute // Route 객체들 경로 확인!
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MoviesRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.SignUpRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MovieDetailRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.LoginRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.SignUpRoute
// LoginScreen, SignUpScreen, MoviesScreenContent 등도 import 필요 (별도 파일로 분리 후)
// 예: import com.TYTgoogle.TYTfirebase.TYTexample.ui.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun AppContent(
    mainViewModel: MainViewModel,
    auth: FirebaseAuth
) {
    val isSplashVisible by mainViewModel.isSplashVisible.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()

    // --- MainActivity에서 가져온 UI 로직 전체 ---
    FirebaseDataConnectTheme {
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        if (isSplashVisible) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...") // 실제 스플래시 UI로 대체 권장
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackBarHostState) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = if (currentUser != null) MoviesRoute else LoginRoute,
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    composable<LoginRoute> {
                        // LoginScreen 호출 할려면  내부에 auth 가  필히    전달  되어야한다
                        LoginScreen(
                            auth = auth, //  로그인  스크린을  런칭  하기  위해서는  이 인증  어셔가  내주에  전달이  포함  되어야  로그인   을  실행시키는  조건이  있다  여기에 auth 객체를 전달합니다.

                            onLoginSuccess = {
                                navController.navigate(MoviesRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToSignUp = {
                                navController.navigate(SignUpRoute)
                            }
                        )
                    }
                    composable<MoviesRoute> {
                        if (currentUser != null) {
                            // MoviesScreenContent 호출
                            // MoviesScreenContent(...)
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Movies Screen Placeholder - User: ${currentUser?.email}")
                            }
                        } else {
                            Log.d("NavHost", "MoviesRoute reached but currentUser is null.")
                        }
                    }
                    composable<MovieDetailRoute> { backStackEntry ->
                        if (currentUser != null) {
                            val movieDetailArgs = backStackEntry.toRoute<MovieDetailRoute>()
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Movie Detail Screen for ID: ${movieDetailArgs.movieId}")
                            }
                        }
                    }
                }

                    LaunchedEffect(currentUser, navController, isSplashVisible) {
                        if (!isSplashVisible) {
                            val currentRouteObject = navController.currentBackStackEntry?.toRoute<Any?>()
                            Log.d("AppContentNavEffect", "User: ${currentUser?.email}, Current Route: $currentRouteObject, Splash: $isSplashVisible")

                            if (currentUser != null) { // 로그인 된 상태
                                // 현재 화면이 Login 또는 SignUp 화면이라면 Movies 화면으로 이동
                                if (currentRouteObject is LoginRoute || currentRouteObject is SignUpRoute) {
                                    Log.d("AppContentNavEffect", "User logged in. Navigating to Movies from $currentRouteObject")
                                    navController.navigate(MoviesRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                // (선택 사항) 만약 currentRouteObject가 null (초기 상태, 아직 아무 화면도 로드되지 않음)이고
                                // 사용자가 이미 로그인되어 있다면 MoviesRoute로 보낼 수 있습니다.
                                // 하지만 NavHost의 startDestination이 이 역할을 이미 하고 있을 가능성이 높습니다.
                                // else if (currentRouteObject == null) {
                                //     Log.d("AppContentNavEffect", "User logged in (initial). Navigating to Movies.")
                                //     navController.navigate(MoviesRoute) {
                                //         popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                //         launchSingleTop = true
                                //     }
                                // }

                            } else { // 로그아웃 된 상태 또는 초기 상태 (currentUser가 null)
                                // 현재 화면이 Login 또는 SignUp 화면이 아니라면 Login 화면으로 이동
                                if (currentRouteObject !is LoginRoute && currentRouteObject !is SignUpRoute) {
                                    Log.d("AppContentNavEffect", "User not logged in. Navigating to Login from $currentRouteObject")
                                    navController.navigate(LoginRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                // (선택 사항) 만약 currentRouteObject가 null이고 사용자가 로그인되어 있지 않다면
                                // LoginRoute로 보낼 수 있습니다.
                                // NavHost의 startDestination이 이 역할을 이미 하고 있을 가능성이 높습니다.
                                // else if (currentRouteObject == null) {
                                //     Log.d("AppContentNavEffect", "User not logged in (initial). Navigating to Login.")
                                //     navController.navigate(LoginRoute) {
                                //         popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                //         launchSingleTop = true
                                //     }
                                // }
                            }
                        }
                    }
            }
        }
    }
    // --- MainActivity에서 가져온 UI 로직 전체 끝 ---
}