package com.tindatracker.app.ui.screens.compare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tindatracker.app.TindaTrackerApplication
import com.tindatracker.app.data.model.PriceComparison
import com.tindatracker.app.data.model.Product
import com.tindatracker.app.data.model.ProductWithBestPrice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompareUiState(
    val allProducts      : List<ProductWithBestPrice> = emptyList(),
    val selectedProduct  : Product?                   = null,
    val priceComparisons : List<PriceComparison>      = emptyList(),
    val isLoading        : Boolean                    = true,
    val showUpdateDialog : Boolean                    = false,
    val successMessage   : String?                    = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CompareViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as TindaTrackerApplication).repository

    private val _selectedProductId = MutableStateFlow<Long?>(null)
    private val _showDialog        = MutableStateFlow(false)
    private val _successMsg        = MutableStateFlow<String?>(null)

    private val allProductsFlow: Flow<List<ProductWithBestPrice>> =
        repo.getProductsWithBestPrice()

    private val comparisonsFlow: Flow<List<PriceComparison>> =
        _selectedProductId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repo.getPriceComparisons(id)
        }

    val uiState: StateFlow<CompareUiState> =
        combine(
            allProductsFlow,
            _selectedProductId,
            comparisonsFlow,
            _showDialog,
            _successMsg
        ) { products, selId, comparisons, showDialog, successMsg ->
            val selProduct = products.firstOrNull { it.product.id == selId }?.product
            CompareUiState(
                allProducts      = products,
                selectedProduct  = selProduct,
                priceComparisons = comparisons,
                isLoading        = false,
                showUpdateDialog = showDialog,
                successMessage   = successMsg
            )
        }.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = CompareUiState()
        )

    /** Called by ProductsScreen (or internally) to pre-select a product. */
    fun selectProduct(productId: Long) { _selectedProductId.value = productId }

    fun clearSelection()   { _selectedProductId.value = null }
    fun showUpdateDialog() { _showDialog.value = true }
    fun hideUpdateDialog() { _showDialog.value = false }
    fun clearSuccessMsg()  { _successMsg.value = null }

    fun updatePrice(storeId: Long, newPrice: Double) {
        val productId = _selectedProductId.value ?: return
        viewModelScope.launch {
            repo.updatePrice(productId, storeId, newPrice)
            _showDialog.value  = false
            _successMsg.value  = "Presyo na-update! ✅"
        }
    }
}
