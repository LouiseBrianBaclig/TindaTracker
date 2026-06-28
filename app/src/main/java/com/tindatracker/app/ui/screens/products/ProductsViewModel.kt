package com.tindatracker.app.ui.screens.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tindatracker.app.TindaTrackerApplication
import com.tindatracker.app.data.model.ProductCategory
import com.tindatracker.app.data.model.ProductWithBestPrice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class ProductsUiState(
    val products         : List<ProductWithBestPrice> = emptyList(),
    val selectedCategory : String                     = ProductCategory.ALL,
    val searchQuery      : String                     = "",
    val isLoading        : Boolean                    = true
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ProductsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as TindaTrackerApplication).repository

    private val _query    = MutableStateFlow("")
    private val _category = MutableStateFlow(ProductCategory.ALL)

    val searchQuery    : StateFlow<String> = _query.asStateFlow()
    val selectedCategory: StateFlow<String> = _category.asStateFlow()

    val uiState: StateFlow<ProductsUiState> =
        combine(_query.debounce(250), _category) { q, cat -> q to cat }
            .flatMapLatest { (q, cat) ->
                repo.getProductsWithBestPrice(category = cat, query = q)
                    .map { products ->
                        ProductsUiState(
                            products         = products,
                            selectedCategory = cat,
                            searchQuery      = q,
                            isLoading        = false
                        )
                    }
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProductsUiState()
            )

    fun setSearchQuery(q: String)     { _query.value    = q }
    fun setCategory(cat: String)      { _category.value = cat }
}
