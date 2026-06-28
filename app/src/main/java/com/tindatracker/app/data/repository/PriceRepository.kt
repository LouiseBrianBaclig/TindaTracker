package com.tindatracker.app.data.repository

import com.tindatracker.app.data.SampleData
import com.tindatracker.app.data.db.AppDatabase
import com.tindatracker.app.data.model.*
import kotlinx.coroutines.flow.*

class PriceRepository(db: AppDatabase) {

    private val storeDao       = db.storeDao()
    private val productDao     = db.productDao()
    private val priceEntryDao  = db.priceEntryDao()
    private val shoppingListDao = db.shoppingListDao()

    // ── Initialise sample data on first launch ───────────────────────────────
    suspend fun initializeSampleDataIfEmpty() {
        if (storeDao.getCount() == 0) {
            storeDao.insertAll(SampleData.stores)
            productDao.insertAll(SampleData.products)
            priceEntryDao.insertAll(SampleData.priceEntries)
        }
    }

    // ── Store queries ────────────────────────────────────────────────────────
    fun getAllStores(): Flow<List<Store>> = storeDao.getAllStores()

    // ── Product queries ──────────────────────────────────────────────────────
    fun getProductCount(): Flow<Int> = productDao.countFlow()

    /** Returns every product annotated with its best (lowest) and highest price. */
    fun getProductsWithBestPrice(
        category: String = ProductCategory.ALL,
        query: String    = ""
    ): Flow<List<ProductWithBestPrice>> =
        combine(
            if (query.isBlank()) {
                if (category == ProductCategory.ALL) productDao.getAllProducts()
                else productDao.getByCategory(category)
            } else {
                productDao.search(query)
            },
            priceEntryDao.getAllEntries(),
            storeDao.getAllStores()
        ) { products, prices, stores ->
            val storeMap = stores.associateBy { it.id }
            products.map { product ->
                val productPrices = prices.filter { it.productId == product.id }
                val minEntry   = productPrices.minByOrNull { it.price }
                val maxPrice   = productPrices.maxOfOrNull { it.price } ?: 0.0
                val bestStore  = minEntry?.let { storeMap[it.storeId] }
                ProductWithBestPrice(
                    product       = product,
                    bestPrice     = minEntry?.price ?: 0.0,
                    bestStoreName = bestStore?.shortName ?: "—",
                    storeEmoji    = bestStore?.emoji ?: "🏪",
                    highestPrice  = maxPrice
                )
            }
        }

    /** Price comparisons for a single product across all stores, cheapest first. */
    fun getPriceComparisons(productId: Long): Flow<List<PriceComparison>> =
        combine(
            priceEntryDao.getForProduct(productId),
            storeDao.getAllStores()
        ) { entries, stores ->
            val storeMap  = stores.associateBy { it.id }
            val minPrice  = entries.minOfOrNull { it.price } ?: 0.0
            entries.map { entry ->
                val store = storeMap[entry.storeId] ?: return@map null
                PriceComparison(
                    store        = store,
                    price        = entry.price,
                    isOnSale     = entry.isOnSale,
                    saleLabel    = entry.saleLabel,
                    isBestPrice  = entry.price == minPrice,
                    dateRecorded = entry.dateRecorded
                )
            }.filterNotNull().sortedBy { it.price }
        }

    /** Best deals across ALL products — biggest absolute savings. */
    fun getBestDeals(limit: Int = 6): Flow<List<ProductWithBestPrice>> =
        getProductsWithBestPrice().map { list ->
            list.filter { it.hasPriceVariation }
                .sortedByDescending { it.savings }
                .take(limit)
        }

    suspend fun updatePrice(productId: Long, storeId: Long, newPrice: Double) {
        priceEntryDao.insert(
            PriceEntry(
                productId    = productId,
                storeId      = storeId,
                price        = newPrice,
                dateRecorded = System.currentTimeMillis()
            )
        )
    }

    // ── Shopping list ────────────────────────────────────────────────────────
    fun getShoppingListWithDetails(): Flow<List<ShoppingListWithDetails>> =
        combine(
            shoppingListDao.getAllItems(),
            productDao.getAllProducts(),
            priceEntryDao.getAllEntries(),
            storeDao.getAllStores()
        ) { items, products, prices, stores ->
            val productMap = products.associateBy { it.id }
            val storeMap   = stores.associateBy   { it.id }
            items.mapNotNull { item ->
                val product    = productMap[item.productId] ?: return@mapNotNull null
                val best       = prices.filter { it.productId == product.id }.minByOrNull { it.price }
                val bestStore  = best?.let { storeMap[it.storeId] }
                ShoppingListWithDetails(
                    item          = item,
                    product       = product,
                    bestPrice     = best?.price ?: 0.0,
                    bestStoreName = bestStore?.shortName ?: "—"
                )
            }
        }

    suspend fun addToShoppingList(productId: Long, quantity: Int = 1) {
        val existing = shoppingListDao.getByProductId(productId)
        if (existing != null) {
            shoppingListDao.update(existing.copy(quantity = existing.quantity + quantity))
        } else {
            shoppingListDao.insert(ShoppingListItem(productId = productId, quantity = quantity))
        }
    }

    suspend fun removeFromShoppingList(itemId: Long) = shoppingListDao.deleteById(itemId)

    suspend fun toggleChecked(item: ShoppingListItem) =
        shoppingListDao.update(item.copy(isChecked = !item.isChecked))

    suspend fun updateQuantity(item: ShoppingListItem, newQty: Int) {
        if (newQty <= 0) shoppingListDao.deleteById(item.id)
        else shoppingListDao.update(item.copy(quantity = newQty))
    }

    suspend fun clearCheckedItems() = shoppingListDao.deleteChecked()
}
