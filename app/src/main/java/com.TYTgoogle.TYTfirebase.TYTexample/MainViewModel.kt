package com.TYTgoogle.TYTfirebase.TYTexample



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // API 직접 임포트
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.TYTgoogle.TYTfirebase.TYTexample.ui.LoginRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.LoginScreen
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MovieDetailRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MovieDetailScreen
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MoviesRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.MoviesScreen
import com.TYTgoogle.TYTfirebase.TYTexample.ui.SignUpRoute
import com.TYTgoogle.TYTfirebase.TYTexample.ui.SignUpScreen
import com.TYTgoogle.TYTfirebase.TYTexample.ui.theme.FirebaseDataConnectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 간단한 ViewModel 예시 (상태 관리를 위해)
class
MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    // 스플래시 화면을 보여줄지 여부를 결정하는 내부 상태
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // ViewModel이 생성될 때 인증 상태를 확인하고 스플래시 상태 업데이트
        viewModelScope.launch {
            // 예: 최소 스플래시 표시 시간 (선택 사항)
            // delay(1000)

            // Firebase Auth 상태를 구독
            auth.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
                // 인증 상태가 결정되면 스플래시를 더 이상 보여주지 않음
                _isSplashVisible.value = false
            }

            // 초기 currentUser 값을 한 번 확인 (리스너가 등록되기 전에 이미 로그인된 경우 대비)
            // 또는 리스너가 충분히 빨리 반응한다고 가정할 수도 있습니다.
            // 만약 리스너가 등록되기 전에 이미 로그인 상태가 확정적이라면,
            // _isSplashVisible.value = false를 즉시 호출할 수도 있습니다.
            // 여기서는 AuthStateListener가 초기 상태도 잘 처리해 줄 것으로 기대합니다.
            // 첫 상태 확인 후 바로 스플래시 종료 조건 (더 빠른 반응)
            if (_currentUser.value != null || auth.currentUser == null) { // 사용자가 있거나, 없는게 확실해지면
                _isSplashVisible.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}