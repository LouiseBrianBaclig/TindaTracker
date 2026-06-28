package com.tindatracker.app.ui.screens.shopping

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tindatracker.app.TindaTrackerApplication
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.data.model.ShoppingListItem
import com.tindatracker.app.data.model.ShoppingListWithDetails
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ShoppingListUiState(
    val listItems        : List<ShoppingListWithDetails> = emptyList(),
    val allProducts      : List<ProductWithBestPrice>    = emptyList(),
    val totalCost        : Double                        = 0.0,
    val checkedCount     : Int                           = 0,
    val showAddDialog    : Boolean                       = false,
    val isLoading        : Boolean                       = true
)

class ShoppingListViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as TindaTrackerApplication).repository

    private val _showAddDialog = MutableStateFlow(false)

    val uiState: StateFlow<ShoppingListUiState> =
        combine(
            repo.getShoppingListWithDetails(),
            repo.getProductsWithBestPrice(),
            _showAddDialog
        ) { items, products, showDialog ->
            ShoppingListUiState(
                listItems     = items,
                allProducts   = products,
                totalCost     = items.sumOf { it.lineTotal },
                checkedCount  = items.count { it.item.isChecked },
                showAddDialog = showDialog,
                isLoading     = false
            )
        }.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShoppingListUiState()
        )

    fun showAddDialog()  { _showAddDialog.value = true  }
    fun hideAddDialog()  { _showAddDialog.value = false }

    fun addProduct(productId: Long, quantity: Int) {
        viewModelScope.launch {
            repo.addToShoppingList(productId, quantity)
            _showAddDialog.value = false
        }
    }

    fun removeItem(itemId: Long) {
        viewModelScope.launch { repo.removeFromShoppingList(itemId) }
    }

    fun toggleChecked(item: ShoppingListItem) {
        viewModelScope.launch { repo.toggleChecked(item) }
    }

    fun incrementQty(item: ShoppingListItem) {
        viewModelScope.launch { repo.updateQuantity(item, item.quantity + 1) }
    }

    fun decrementQty(item: ShoppingListItem) {
        viewModelScope.launch { repo.updateQuantity(item, item.quantity - 1) }
    }

    fun clearChecked() {
        viewModelScope.launch { repo.clearCheckedItems() }
    }
}
