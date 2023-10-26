package com.arieldywelski.movieschallenge.api

import com.arieldywelski.movieschallenge.data.Movie
import com.arieldywelski.movieschallenge.data.MoviesResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPIService {
  @GET("movie/now_playing")
  suspend fun getNowPlayingMovies(
    @Query("page") page: Int,
    @Query("language") language: String = QUERY_LANGUAGE
  ): MoviesResponse

  @GET("search/movie")
  suspend fun getSearchMovies(
    @Query("query") query: String,
    @Query("page") page: Int,
    @Query("language") language: String = QUERY_LANGUAGE
  ): MoviesResponse

  @GET("movie/{movie_id}")
  suspend fun getMovieDetails(
    @Path("movie_id") movieId: Int,
    @Query("language") language: String = QUERY_LANGUAGE
  ): retrofit2.Response<Movie>

  companion object {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val QUERY_LANGUAGE = "pl"

    fun create(): MovieAPIService {
      val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

      val client = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(logger)
        .build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieAPIService::class.java)
    }
  }
}

class HeaderInterceptor : Interceptor {

  companion object {
    private const val AUTHENTICATION_HEADER_NAME = "Authorization"
    private const val ACCESS_TOKEN =
      "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzYWQ1NDNiZjMzYjk0OGY5NWM4YjhiZDA1YjMxNWMzMyIsInN1YiI6IjVlODhhYWU1NmM3NGI5MDAxMzk1OTM3NSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.O6orzBaZKMxMTLq8KdMie0D2AttzFn8u49I2OiskZUQ"
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()

    request = request.newBuilder().addHeader(
      name = AUTHENTICATION_HEADER_NAME,
      value = ACCESS_TOKEN
    ).build()

    return chain.proceed(request)
  }
}