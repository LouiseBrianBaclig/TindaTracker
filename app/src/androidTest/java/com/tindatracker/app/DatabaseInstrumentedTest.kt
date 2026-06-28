package com.tindatracker.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tindatracker.app.data.SampleData
import com.tindatracker.app.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for Room DAO operations.
 * Run with: ./gradlew :app:connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insertAndReadStores() = runBlocking {
        db.storeDao().insertAll(SampleData.stores)
        val stores = db.storeDao().getAllStores().first()
        assertEquals(5, stores.size)
    }

    @Test
    fun insertAndReadProducts() = runBlocking {
        db.productDao().insertAll(SampleData.products)
        val products = db.productDao().getAllProducts().first()
        assertEquals(20, products.size)
    }

    @Test
    fun insertAndReadPriceEntries() = runBlocking {
        db.storeDao().insertAll(SampleData.stores)
        db.productDao().insertAll(SampleData.products)
        db.priceEntryDao().insertAll(SampleData.priceEntries)

        val entries = db.priceEntryDao().getAllEntries().first()
        assertEquals(100, entries.size)
    }

    @Test
    fun replaceStrategy_updatesExistingPrice() = runBlocking {
        db.storeDao().insertAll(SampleData.stores)
        db.productDao().insertAll(SampleData.products)
        db.priceEntryDao().insertAll(SampleData.priceEntries)

        // Insert a new price for productId=1, storeId=1 — should REPLACE the old one
        db.priceEntryDao().insert(
            com.tindatracker.app.data.model.PriceEntry(productId = 1L, storeId = 1L, price = 19.99)
        )

        val entriesForProduct1 = db.priceEntryDao().getForProduct(1L).first()
        // Still 5 entries (one per store), not 6
        assertEquals(5, entriesForProduct1.size)
        // SM price updated to 19.99
        val smEntry = entriesForProduct1.firstOrNull { it.storeId == 1L }
        assertNotNull(smEntry)
        assertEquals(19.99, smEntry!!.price, 0.001)
    }

    @Test
    fun shoppingList_addAndToggle() = runBlocking {
        db.productDao().insertAll(SampleData.products)

        val listDao = db.shoppingListDao()
        listDao.insert(
            com.tindatracker.app.data.model.ShoppingListItem(productId = 1L, quantity = 3)
        )

        val items = listDao.getAllItems().first()
        assertEquals(1, items.size)
        assertEquals(3, items[0].quantity)
        assertFalse(items[0].isChecked)

        // Toggle checked
        listDao.update(items[0].copy(isChecked = true))
        val updated = listDao.getAllItems().first()
        assertTrue(updated[0].isChecked)

        // Delete checked
        listDao.deleteChecked()
        val afterClear = listDao.getAllItems().first()
        assertTrue(afterClear.isEmpty())
    }

    @Test
    fun storeCount_isZero_onFreshDatabase() = runBlocking {
        val count = db.storeDao().getCount()
        assertEquals(0, count)
    }
}
