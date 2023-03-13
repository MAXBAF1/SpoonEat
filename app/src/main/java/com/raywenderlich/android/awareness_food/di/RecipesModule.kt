/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.awareness_food.di

import android.content.Context
import com.raywenderlich.android.awareness_food.analytics.AnalyticsEvents
import com.raywenderlich.android.awareness_food.analytics.AnalyticsEventsImpl
import com.raywenderlich.android.awareness_food.network.AllFoodService
import com.raywenderlich.android.awareness_food.network.RecipesService
import com.raywenderlich.android.awareness_food.repositories.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val API_KEY = "f1a7af1bc1744b7c8fd25cb13f716aef"

@Module
class RecipesModule(val context: Context) {

  @Singleton
  @Provides
  fun providesRetrofitService(): AllFoodService {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
          val url = chain.request().url().newBuilder()
              .addQueryParameter("apiKey", API_KEY)
              .build()

          val requestBuilder = chain.request().newBuilder().url(url)
          chain.proceed(requestBuilder.build())
        }
        .build()
    val builder = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
    return builder.build().create(AllFoodService::class.java)
  }

  @Singleton
  @Provides
  fun providesRecipeRepository(recipesService: AllFoodService): AllFoodRepository = AllFoodRepositoryImpl(recipesService)

//  @Singleton
//  @Provides
//  fun providesTriviaRepository(recipesService: AllFoodService): AllFoodRepository = AllFoodRepositoryImpl(recipesService)

  @Singleton
  @Provides
  fun providesContext() = context

  @Singleton
  @Provides
  fun providesAnalytics(): AnalyticsEvents = AnalyticsEventsImpl()
}