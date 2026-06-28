package com.tindatracker.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tindatracker.app.TindaTrackerApplication
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.data.model.Store
import kotlinx.coroutines.flow.*

data class HomeUiState(
    val bestDeals     : List<ProductWithBestPrice> = emptyList(),
    val stores        : List<Store>                = emptyList(),
    val productCount  : Int                        = 0,
    val isLoading     : Boolean                    = true
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as TindaTrackerApplication).repository

    val uiState: StateFlow<HomeUiState> = combine(
        repo.getBestDeals(limit = 8),
        repo.getAllStores(),
        repo.getProductCount()
    ) { deals, stores, count ->
        HomeUiState(
            bestDeals    = deals,
            stores       = stores,
            productCount = count,
            isLoading    = false
        )
    }.stateIn(
        scope         = viewModelScope,
        started       = SharingStarted.WhileSubscribed(5_000),
        initialValue  = HomeUiState()
    )
}
