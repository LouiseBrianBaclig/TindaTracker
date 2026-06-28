package com.tindatracker.app.data

import com.tindatracker.app.data.model.ProductCategory
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the pre-loaded sample data and model invariants.
 * Run with: ./gradlew :app:test
 */
class SampleDataTest {

    @Test
    fun storeCount_isExactlyFive() {
        assertEquals(
            "Expected exactly 5 supermarket chains",
            5,
            SampleData.stores.size
        )
    }

    @Test
    fun productCount_isExactlyTwenty() {
        assertEquals(
            "Expected exactly 20 products",
            20,
            SampleData.products.size
        )
    }

    @Test
    fun priceEntries_hasOnePerProductStorePair() {
        val expected = SampleData.products.size * SampleData.stores.size   // 20 × 5 = 100
        assertEquals(
            "Expected one price entry per (product, store) pair",
            expected,
            SampleData.priceEntries.size
        )
    }

    @Test
    fun allPrices_arePositive() {
        SampleData.priceEntries.forEach { entry ->
            assertTrue(
                "Price must be positive for productId=${entry.productId}, storeId=${entry.storeId}",
                entry.price > 0.0
            )
        }
    }

    @Test
    fun stores_haveUniqueIds() {
        val ids = SampleData.stores.map { it.id }
        assertEquals("Store IDs must be unique", ids.distinct().size, ids.size)
    }

    @Test
    fun products_haveUniqueIds() {
        val ids = SampleData.products.map { it.id }
        assertEquals("Product IDs must be unique", ids.distinct().size, ids.size)
    }

    @Test
    fun products_allHaveValidCategories() {
        val validCategories = setOf(
            ProductCategory.CANNED_GOODS,
            ProductCategory.INSTANT_NOODLES,
            ProductCategory.BEVERAGES,
            ProductCategory.CONDIMENTS,
            ProductCategory.PERSONAL_CARE
        )
        SampleData.products.forEach { product ->
            assertTrue(
                "Product '${product.name}' has unknown category '${product.category}'",
                product.category in validCategories
            )
        }
    }

    @Test
    fun priceEntries_allReferenceValidProductIds() {
        val validProductIds = SampleData.products.map { it.id }.toSet()
        SampleData.priceEntries.forEach { entry ->
            assertTrue(
                "PriceEntry references unknown productId=${entry.productId}",
                entry.productId in validProductIds
            )
        }
    }

    @Test
    fun priceEntries_allReferenceValidStoreIds() {
        val validStoreIds = SampleData.stores.map { it.id }.toSet()
        SampleData.priceEntries.forEach { entry ->
            assertTrue(
                "PriceEntry references unknown storeId=${entry.storeId}",
                entry.storeId in validStoreIds
            )
        }
    }

    @Test
    fun sardines_puregoldIsAlwaysCheapest() {
        // Ligo Sardines (productId = 1): Puregold (storeId = 3) should be cheapest
        val sardineEntries = SampleData.priceEntries.filter { it.productId == 1L }
        val cheapest = sardineEntries.minByOrNull { it.price }
        assertEquals(
            "Puregold (storeId=3) should be cheapest for Ligo Sardines",
            3L,
            cheapest?.storeId
        )
    }

    @Test
    fun allDay_isNeverCheapest_forCannedGoods() {
        // AllDay (storeId = 5) has premium prices — never cheapest for canned goods
        val cannedProductIds = SampleData.products
            .filter { it.category == ProductCategory.CANNED_GOODS }
            .map { it.id }
            .toSet()

        val cheapestStoreIds = cannedProductIds.map { productId ->
            SampleData.priceEntries
                .filter { it.productId == productId }
                .minByOrNull { it.price }
                ?.storeId
        }

        assertFalse(
            "AllDay should never be cheapest for canned goods in sample data",
            5L in cheapestStoreIds
        )
    }

    @Test
    fun categoryEmojiReturnsCorrectEmojis() {
        assertEquals("🥫", ProductCategory.emoji(ProductCategory.CANNED_GOODS))
        assertEquals("🍜", ProductCategory.emoji(ProductCategory.INSTANT_NOODLES))
        assertEquals("☕", ProductCategory.emoji(ProductCategory.BEVERAGES))
        assertEquals("🧂", ProductCategory.emoji(ProductCategory.CONDIMENTS))
        assertEquals("🧴", ProductCategory.emoji(ProductCategory.PERSONAL_CARE))
        assertEquals("🛍️", ProductCategory.emoji(ProductCategory.ALL))
    }
}
