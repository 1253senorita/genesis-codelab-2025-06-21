package  com.TYTgoogle.TYTfirebase.TYTexample.ui.theme


import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
//import com.TYTgoogle.TYTfirebase.TYTexample.ui.theme

// Material 3 Color Schemes (Color.kt 에서 정의된 색상 사용)

// 색상 정의 (Theme.kt 파일 내부에 직접)
val MyCustomPurpleLight = Color(0xFF6200EE) // colors.xml의 purple_500과 동일
val MyCustomPurpleDark = Color(0xFFBB86FC)  // colors.xml의 purple_200과 동일

// 예시 Material 3 색상 이름 (직접 정의)
val Purple40_local = Color(0xFF6650a4)
val PurpleGrey40_local = Color(0xFF625b71)
val Pink40_local = Color(0xFF7D5260)



private val LightColorScheme = lightColorScheme(
    primary = Purple40_local, // 직접 정의한 Purple40_local
    secondary = PurpleGrey40_local,
    tertiary = Pink40_local,
)
@Composable
fun FirebaseDataConnectTheme(
    // 이것이 우리가 사용하려는 Composable 함수입니다.
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Type.kt 에 정의되어 있어야 함
        content = content,
    )
}

