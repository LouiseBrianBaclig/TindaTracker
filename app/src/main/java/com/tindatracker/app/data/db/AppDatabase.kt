package com.tindatracker.app.data.db

import android.content.Context
import androidx.room.*
import com.tindatracker.app.data.model.*
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────
//  DAOs
// ─────────────────────────────────────────────

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY name")
    fun getAllStores(): Flow<List<Store>>

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(stores: List<Store>)

    @Query("SELECT * FROM stores WHERE id = :id")
    suspend fun getById(id: Long): Store?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY category, name")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :cat ORDER BY name")
    fun getByCategory(cat: String): Flow<List<Product>>

    @Query("""
        SELECT * FROM products
        WHERE name  LIKE '%' || :q || '%'
           OR brand LIKE '%' || :q || '%'
        ORDER BY category, name
    """)
    fun search(q: String): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products")
    fun countFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): Product?
}

@Dao
interface PriceEntryDao {
    @Query("SELECT * FROM price_entries")
    fun getAllEntries(): Flow<List<PriceEntry>>

    @Query("SELECT * FROM price_entries WHERE product_id = :productId ORDER BY price ASC")
    fun getForProduct(productId: Long): Flow<List<PriceEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)   // REPLACE enforces unique(product_id, store_id)
    suspend fun insertAll(entries: List<PriceEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PriceEntry)
}

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM shopping_list_items WHERE product_id = :productId LIMIT 1")
    suspend fun getByProductId(productId: Long): ShoppingListItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingListItem)

    @Update
    suspend fun update(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM shopping_list_items WHERE is_checked = 1")
    suspend fun deleteChecked()

    @Query("DELETE FROM shopping_list_items")
    suspend fun clearAll()
}

// ─────────────────────────────────────────────
//  DATABASE
// ─────────────────────────────────────────────

@Database(
    entities = [Store::class, Product::class, PriceEntry::class, ShoppingListItem::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao
    abstract fun priceEntryDao(): PriceEntryDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tinda_tracker.db"
                ).build().also { INSTANCE = it }
            }
    }
}
