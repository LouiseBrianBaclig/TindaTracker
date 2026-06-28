package com.tindatracker.app.data.model

import androidx.room.*

// ─────────────────────────────────────────────
//  ROOM ENTITIES
// ─────────────────────────────────────────────

@Entity(tableName = "stores")
data class Store(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val shortName: String,
    val emoji: String,
    val colorHex: String,     // e.g. "#1565C0"
    val description: String = ""
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,
    val unit: String,           // e.g. "155g", "1L", "piece"
    val imageEmoji: String = "🛍️"
)

@Entity(
    tableName = "price_entries",
    indices = [Index(value = ["product_id", "store_id"], unique = true)]
)
data class PriceEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "store_id")   val storeId: Long,
    val price: Double,
    @ColumnInfo(name = "date_recorded") val dateRecorded: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_on_sale")    val isOnSale: Boolean = false,
    @ColumnInfo(name = "sale_label")    val saleLabel: String = ""
)

@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_id") val productId: Long,
    val quantity: Int = 1,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false
)

// ─────────────────────────────────────────────
//  UI MODELS  (not stored in Room)
// ─────────────────────────────────────────────

/** A product enriched with its cheapest and most-expensive store price. */
data class ProductWithBestPrice(
    val product: Product,
    val bestPrice: Double,
    val bestStoreName: String,
    val storeEmoji: String,
    val highestPrice: Double
) {
    val savings: Double          get() = if (highestPrice > bestPrice) highestPrice - bestPrice else 0.0
    val hasPriceVariation: Boolean get() = savings > 0.50
}

/** One row in the price-comparison table for a single product. */
data class PriceComparison(
    val store: Store,
    val price: Double,
    val isOnSale: Boolean = false,
    val saleLabel: String = "",
    val isBestPrice: Boolean = false,
    val dateRecorded: Long = System.currentTimeMillis()
)

/** Shopping-list item enriched with product info and best price. */
data class ShoppingListWithDetails(
    val item: ShoppingListItem,
    val product: Product,
    val bestPrice: Double,
    val bestStoreName: String
) {
    val lineTotal: Double get() = bestPrice * item.quantity
}

/** Product categories used for filtering. */
object ProductCategory {
    const val ALL           = "Lahat"
    const val CANNED_GOODS  = "Canned Goods"
    const val INSTANT_NOODLES = "Instant Noodles"
    const val BEVERAGES     = "Beverages"
    const val CONDIMENTS    = "Condiments & Oil"
    const val PERSONAL_CARE = "Personal Care"

    val all = listOf(ALL, CANNED_GOODS, INSTANT_NOODLES, BEVERAGES, CONDIMENTS, PERSONAL_CARE)

    fun emoji(category: String): String = when (category) {
        CANNED_GOODS      -> "🥫"
        INSTANT_NOODLES   -> "🍜"
        BEVERAGES         -> "☕"
        CONDIMENTS        -> "🧂"
        PERSONAL_CARE     -> "🧴"
        else              -> "🛍️"
    }
}
