package com.TYTgoogle.TYTfirebase.TYTexample

import android.os.Bundle
import android.util.Log
// import android.widget.Toast // 사용하지 않으므로 주석 처리 또는 삭제 가능
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
// import androidx.compose.runtime.getValue // 이미 위에서 선언됨
// import androidx.compose.runtime.setValue // 이미 위에서 선언됨
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // AppNavigation에서 MainActivity 내부 함수 호출 시 필요
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
// import androidx.navigation.NavGraph.Companion.findStartDestination // AppNavigation에서 사용될 수 있음
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// 라우트 객체 import 경로 수정 --- 여기가 중요! ---
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.LoginRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.MovieDetailRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.MoviesRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.routes.SignUpRoute
// --- 라우트 객체 import 경로 수정 완료 ---

import com.TYTgoogle.TYTfirebase.TYTexample.ui.theme.FirebaseDataConnectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser // MoviesScreenInternal에서 사용
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
// import com.TYTgoogle.TYTfirebase.TYTexample.ui.AppContent // AppNavigation으로 대체됨

// --- MainViewModel은 별도 파일로 분리되어 있다고 가정하고 import ---
// import com.TYTgoogle.TYTfirebase.TYTexample.MainViewModel // ViewModel 파일 경로에 맞게 수정

// --- 화면 Composable 함수들은 별도 파일로 분리 후 import 권장 ---
// 만약 MainActivity 내부에 화면 Composable을 그대로 둔다면 이 import들은 필요 없습니다.
// AppNavigation에서 activity.LoginScreenInternal 등으로 접근하기 때문입니다.
// 별도 파일로 분리했다면 AppNavigation에서 다음과 같이 import 할 것입니다.
// 예:
// import com.TYTgoogle.TYTfirebase.TYTexample.ui.screens.LoginScreen
// import com.TYTgoogle.TYTfirebase.TYTexample.ui.screens.SignUpScreen
// import com.TYTgoogle.TYTfirebase.TYTexample.ui.screens.MoviesScreen
// import com.TYTgoogle.TYTfirebase.TYTexample.ui.screens.MovieDetailScreen

// MoviesScreen에서 사용하는 데이터 클래스 (별도 파일 또는 MoviesScreen 파일 내부에 정의 권장)
data class MovieItem(val id: String, val title: String) // 이것도 MoviesScreen 관련 파일로 옮기는 것이 좋음
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        // auth.useEmulator("10.0.2.2", 9099)

        setContent {
            FirebaseDataConnectTheme {
                val mainViewModel: MainViewModel = viewModel()
                val isSplashVisible by mainViewModel.isSplashVisible.collectAsState()
                val currentUser by mainViewModel.currentUser.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    splashScreen.setKeepOnScreenCondition {
                        isSplashVisible// isSplashVisible이 Boolean State이므로 .value 사용
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { paddingValues ->
                    AppNavigation(
                        modifier = Modifier.padding(paddingValues),
                        mainViewModel = mainViewModel,
                        auth = auth, // ViewModel에서 인증 처리 권장
                        startDestination = if (currentUser != null) MoviesRoute else LoginRoute,
                        showSnackBar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                    )
                }
            }
        }
    }

    // MainActivity 내에 정의되어 있던 화면 Composable들을 AppNavigation 내부에서 직접 사용하거나,
    // 별도의 파일 (예: ui/screens/LoginScreen.kt)로 옮기고 AppNavigation에서 호출할 수 있습니다.
    // 여기서는 MainActivity 내의 기존 Composable들을 AppNavigation에서 호출하는 형태로 유지합니다.

    // 로그인 화면 Composable (MainActivity 내에 정의된 버전)
    @Composable
    fun LoginScreenInternal(
        // 이름 변경 (AppNavigation의 LoginScreen과 충돌 방지)
        auth: FirebaseAuth,
        onLoginSuccess: () -> Unit,
        onNavigateToSignUp: () -> Unit,
        showSnackBar: (String) -> Unit,
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Log.d("LoginScreen", "Firebase SignIn: Success")
                                        onLoginSuccess() // ViewModel에서 currentUser 업데이트 유도 및 스낵바 표시
                                    } else {
                                        Log.w(
                                            "LoginScreen",
                                            "Firebase SignIn: Failure",
                                            task.exception
                                        )
                                        showSnackBar("Login Failed: ${task.exception?.message}")
                                    }
                                }
                        } else {
                            showSnackBar("Please enter email and password.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Login")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign Up")
            }
        }
    }

    // 회원가입 화면 Composable (MainActivity 내에 정의된 버전)
    @Composable
    fun SignUpScreenInternal(
        // 이름 변경
        auth: FirebaseAuth,
        onSignUpSuccess: () -> Unit,
        onNavigateToLogin: () -> Unit,
        showSnackBar: (String) -> Unit,
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                            if (password == confirmPassword) {
                                isLoading = true
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            Log.d("SignUpScreen", "Firebase CreateUser: Success")
                                            onSignUpSuccess() // 스낵바 표시 및 네비게이션
                                        } else {
                                            Log.w(
                                                "SignUpScreen",
                                                "Firebase CreateUser: Failure",
                                                task.exception
                                            )
                                            showSnackBar("Sign Up Failed: ${task.exception?.message}")
                                        }
                                    }
                            } else {
                                showSnackBar("Passwords do not match.")
                            }
                        } else {
                            showSnackBar("Please fill in all fields.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Sign Up")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }
    }


    // MoviesScreenContent Composable (MainActivity 내에 정의된 버전)
    // 이 함수는 AppNavigation 내에서 MoviesRoute에 직접 연결될 MoviesScreen에 해당합니다.
    // 여기서는 이름을 MoviesScreenInternal로 변경하고, AppNavigation에서 이를 호출하도록 합니다.
    @Composable
    fun MoviesScreenInternal(
        // 이름 변경 및 파라미터 조정
        // navController: NavController, // AppNavigation에서 관리하므로 직접 필요 X
        // auth: FirebaseAuth, // ViewModel에서 관리
        user: FirebaseUser?, // ViewModel의 currentUser 사용
        mainViewModel: MainViewModel,
        onNavigateToMovieDetail: (String) -> Unit, // 상세 화면 이동 콜백
        showSnackBar: (String) -> Unit, // 스낵바 표시용
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text("Welcome, ${user?.email ?: "Guest"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    mainViewModel.signOut() // ViewModel 통해 로그아웃
                    Log.d("MoviesScreen", "Sign out clicked.")
                    showSnackBar("로그아웃 되었습니다.")
                    // 네비게이션은 AppNavigation의 LaunchedEffect(currentUser)가 처리
                },
            ) {
                Text("Sign Out")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onNavigateToMovieDetail("exampleMovie123") // 콜백 호출
                },
            ) {
                Text("View Dummy Movie Detail")
            }
            // 여기에 실제 영화 목록 등을 표시하는 UI 추가 가능
        }
    }

    // MovieDetailScreen (별도 파일로 분리하는 것을 강력히 권장)
    // 여기서는 간단히 AppNavigation 내부에 정의합니다.
    @Composable
    fun MovieDetailScreenInternal(movieId: String, onNavigateBack: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Movie Detail Screen", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Showing details for movie ID: $movieId")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Back to Movies")
            }
        }
    }
}


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    auth: FirebaseAuth,
    startDestination: Any,
    showSnackBar: (String) -> Unit,
) {
    val currentUser by mainViewModel.currentUser.collectAsState()
    val activity = LocalContext.current as MainActivity // MainActivity의 내부 Composable 접근용

    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser != null) {
            // MoviesRoute의 실제 경로 문자열과 비교. 타입-세이프에서는 @Serializable 객체의 경로를 사용.
            // 여기서는 MoviesRoute 객체를 직접 비교하기보다는,
            // 네비게이션 그래프에 등록된 경로 문자열과 비교해야 합니다.
            // toRoute<MoviesRoute>().toString() 등은 컴파일 타임 경로가 아니므로 주의.
            // 가장 간단한 방법은 LoginRoute일 때만 MoviesRoute로 보내는 것입니다.
            if (currentRoute == LoginRoute::class.qualifiedName || startDestination == LoginRoute) {
                navController.navigate(MoviesRoute) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else { // currentUser == null (로그아웃 상태)
            // 현재 Movies 관련 화면에 있다면 로그인 화면으로 이동
            if (currentRoute != LoginRoute::class.qualifiedName && currentRoute != SignUpRoute::class.qualifiedName) {
                navController.navigate(LoginRoute) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<LoginRoute> {
            activity.LoginScreenInternal(
                // MainActivity의 Composable 호출
                auth = auth,
                onLoginSuccess = {
                    // ViewModel에서 currentUser를 업데이트하면 LaunchedEffect가 감지
                    // mainViewModel.refreshCurrentUser() // 필요 시
                    showSnackBar("로그인 성공!")
                    // 네비게이션은 LaunchedEffect가 담당
                },
                onNavigateToSignUp = { navController.navigate(SignUpRoute) },
                showSnackBar = showSnackBar,
            )
        }

        composable<SignUpRoute> {
            activity.SignUpScreenInternal(
                // MainActivity의 Composable 호출
                auth = auth,
                onSignUpSuccess = {
                    showSnackBar("회원가입 성공! 로그인 해주세요.")
                    navController.navigate(LoginRoute) {
                        popUpTo(SignUpRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
                showSnackBar = showSnackBar,
            )
        }

        composable<MoviesRoute> {
            val user = currentUser // 현재 사용자 정보
            activity.MoviesScreenInternal(
                // MainActivity의 Composable 호출
                user = user,
                mainViewModel = mainViewModel,
                onNavigateToMovieDetail = { movieId ->
                    navController.navigate(MovieDetailRoute(movieId = movieId))
                },
                showSnackBar = showSnackBar,
            )
        }

        composable<MovieDetailRoute> { backStackEntry ->
            val movieDetailArgs: MovieDetailRoute = backStackEntry.toRoute()
            activity.MovieDetailScreenInternal(
                // MainActivity의 Composable 호출
                movieId = movieDetailArgs.movieId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}