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

package com.raywenderlich.android.awareness_food

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.SimpleCursorAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.awareness_food.data.Food
import com.raywenderlich.android.awareness_food.databinding.ActivityMainBinding
import com.raywenderlich.android.awareness_food.monitor.NetworkMonitor
import com.raywenderlich.android.awareness_food.monitor.NetworkState
import com.raywenderlich.android.awareness_food.monitor.UnavailableConnectionLifecycleOwner
import com.raywenderlich.android.awareness_food.repositories.AutoCompleteProvider
import com.raywenderlich.android.awareness_food.repositories.models.AllFoodApiState
import com.raywenderlich.android.awareness_food.viewmodels.MainViewModel
import com.raywenderlich.android.awareness_food.viewmodels.UiLoadingState
import com.raywenderlich.android.awareness_food.views.IngredientView
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Main Screen
 * */
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory
  @Inject
  lateinit var unavailableConnectionLifecycleOwner: UnavailableConnectionLifecycleOwner

  private lateinit var networkMonitor: NetworkMonitor
  private val networkObserver = NetworkObserver()

  private val viewModel: MainViewModel by viewModels { viewModelFactory }
  private lateinit var binding: ActivityMainBinding
  private var snackbar: Snackbar? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    networkMonitor = NetworkMonitor(this, lifecycle)
    lifecycle.addObserver(networkMonitor)
    unavailableConnectionLifecycleOwner.addObserver(networkObserver)

    viewModel.allFoodState.observe(this, Observer {
      when (it) {
        AllFoodApiState.Error -> showNetworkUnavailableAlert(R.string.error)
        is AllFoodApiState.Result -> buildViews(it.allFood.searchResults[0].results[0])
      }
    })

    viewModel.loadingState.observe(this, Observer { uiLoadingState ->
      handleLoadingState(uiLoadingState)
    })

    //viewModel.getCurrentRecipe(binding.edRecipeName.text.toString())

    networkMonitor.networkAvailableStateFlow.asLiveData().observe(this, Observer { networkState ->
      handleNetworkState(networkState)
    })

    //autoCompleteTextView()
  }

  private fun autoCompleteTextView() {
    val actv = AutoCompleteTextView(this)
    actv.threshold = 1
    val from = arrayOf("name", "description")
    val to = intArrayOf(android.R.id.text1, android.R.id.text2)
    val a = SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0)
    a.stringConversionColumn = 1

    val provider = AutoCompleteProvider()

    a.filterQueryProvider = provider
    actv.setAdapter(a)
    setContentView(
      actv, ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
      )
    )
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.menu_refresh -> {
      clearViews()
      viewModel.getAllFood(binding.edRecipeName.text.toString())
      true
    }
    R.id.menu_trivia -> {
      startActivity(Intent(this, FoodTriviaActivity::class.java))
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
  }

  private fun buildViews(food: Food) {
    with(binding) {
      recipeInstructionsTitle.text = food.name
//      recipeIngredientsTitle.text = getString(R.string.ingredients)
//      recipeName.text = recipe.title
//      recipeSummary.text = HtmlCompat.fromHtml(recipe.summary, 0)
//      recipeInstructions.text = HtmlCompat.fromHtml(recipe.instructions, 0)
//      Picasso.get().load(food.image).into(recipeImage)
//      food.ingredients.forEachIndexed { index, ingredient ->
//        val ingredientView = IngredientView(this@MainActivity)
//        ingredientView.setIngredient(ingredient)
//        if (index != 0) {
//          ingredientView.addDivider()
//        }
//        recipeIngredients.addView(ingredientView)
//      }
    }
  }

  private fun clearViews() {
    with(binding) {
      recipeName.text = ""
      recipeSummary.text = ""
      recipeInstructions.text = ""
      recipeImage.setImageDrawable(null)
      recipeIngredientsTitle.text = ""
      recipeIngredients.removeAllViews()
      recipeInstructionsTitle.text = ""
    }
  }

  private fun showNetworkUnavailableAlert(message: Int) {
    snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.retry) {
          viewModel.getAllFood(binding.edRecipeName.text.toString())
        }.apply {
          view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
          show()
        }
  }

  private fun handleLoadingState(uiLoadingState: UiLoadingState?) {
    when (uiLoadingState) {
      UiLoadingState.Loading -> {
        clearViews()
        binding.progressBar.isVisible = true
      }
      UiLoadingState.NotLoading -> binding.progressBar.isVisible = false
    }
  }

  private fun handleNetworkState(networkState: NetworkState?) {
    when (networkState) {
      NetworkState.Unavailable -> unavailableConnectionLifecycleOwner.onConnectionLost()
      NetworkState.Available -> unavailableConnectionLifecycleOwner.onConnectionAvailable()
    }
  }

  private fun removeNetworkUnavailableAlert() {
    snackbar?.dismiss()
  }

  inner class NetworkObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onNetworkUnavailable() {
      showNetworkUnavailableAlert(R.string.network_is_unavailable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onNetworkAvailable() {
      removeNetworkUnavailableAlert()
    }
  }
}
