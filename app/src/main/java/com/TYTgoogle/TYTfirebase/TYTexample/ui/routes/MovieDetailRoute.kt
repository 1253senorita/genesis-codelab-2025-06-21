package com.TYTgoogle.TYTfirebase.TYTexample.ui.routes


// 파일 경로: app/src/main/java/com/TYTgoogle/TYTfirebase/TYTexample/ui/routes/Routes.kt
// (또는 com.TYTgoogle.TYTfirebase.TYTexample.ui.Routes.kt 등 편한 위치)



import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object MoviesRoute

@Serializable
object SignUpRoute

@Serializable
data class MovieDetailRoute(val movieId: String) // <--- 바로 이 클래스 정의가 필요합니다!