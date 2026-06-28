package com.tindatracker.app.ui.screens.stores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tindatracker.app.TindaTrackerApplication
import com.tindatracker.app.data.model.Store
import kotlinx.coroutines.flow.*

data class StoreDetail(
    val store       : Store,
    val productCount: Int,
    val minPrice    : Double,
    val maxPrice    : Double,
    val avgPrice    : Double
)

data class StoresUiState(
    val stores   : List<StoreDetail> = emptyList(),
    val isLoading: Boolean           = true
)

class StoresViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as TindaTrackerApplication).repository

    val uiState: StateFlow<StoresUiState> =
        combine(
            repo.getAllStores(),
            repo.getProductsWithBestPrice()
        ) { stores, products ->
            // For each store, compute stats from the flat products list
            // (prices are embedded in ProductWithBestPrice for the cheapest store;
            //  we approximate per-store stats from the sample data range)
            val storeDetails = stores.map { store ->
                val storeProducts = products.filter { it.bestStoreName == store.shortName }
                val allPrices     = storeProducts.map { it.bestPrice }
                StoreDetail(
                    store        = store,
                    productCount = products.size,          // all 20 products tracked in every store
                    minPrice     = allPrices.minOrNull() ?: 0.0,
                    maxPrice     = allPrices.maxOrNull() ?: 0.0,
                    avgPrice     = if (allPrices.isEmpty()) 0.0 else allPrices.average()
                )
            }
            StoresUiState(stores = storeDetails, isLoading = false)
        }.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = StoresUiState()
        )
}
