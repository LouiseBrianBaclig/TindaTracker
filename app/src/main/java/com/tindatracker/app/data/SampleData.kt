package com.tindatracker.app.data

import com.tindatracker.app.data.model.*

/**
 * Pre-loaded data representing real major supermarkets in the Philippines
 * and common sari-sari store stocks with realistic PHP prices (as of 2025-2026).
 */
object SampleData {

    // ── STORES ──────────────────────────────────────────────────────────────
    val stores = listOf(
        Store(id=1, name="SM Supermarket",          shortName="SM",        emoji="🔵", colorHex="#1565C0",
              description="Available in SM Malls nationwide"),
        Store(id=2, name="Robinsons Supermarket",   shortName="Robinsons", emoji="🟠", colorHex="#E65100",
              description="Found inside Robinsons Malls"),
        Store(id=3, name="Puregold",                shortName="Puregold",  emoji="🔴", colorHex="#C62828",
              description="Budget-friendly standalone supermarkets"),
        Store(id=4, name="Waltermart",              shortName="Waltermart",emoji="🟣", colorHex="#4527A0",
              description="Community-focused supermarkets"),
        Store(id=5, name="AllDay Supermarket",      shortName="AllDay",    emoji="🟢", colorHex="#2E7D32",
              description="Premium stores in Ayala Malls")
    )

    // ── PRODUCTS ─────────────────────────────────────────────────────────────
    val products = listOf(
        // Canned Goods
        Product(id=1,  name="Ligo Sardines in Tomato Sauce", brand="Ligo",      category=ProductCategory.CANNED_GOODS,   unit="155g",  imageEmoji="🐟"),
        Product(id=2,  name="Argentina Corned Beef",          brand="Argentina", category=ProductCategory.CANNED_GOODS,   unit="150g",  imageEmoji="🥩"),
        Product(id=3,  name="Century Tuna Hot & Spicy",       brand="Century",   category=ProductCategory.CANNED_GOODS,   unit="155g",  imageEmoji="🐟"),
        Product(id=4,  name="Del Monte Fruit Cocktail",       brand="Del Monte", category=ProductCategory.CANNED_GOODS,   unit="836g",  imageEmoji="🍑"),
        Product(id=5,  name="Mega Sardines in Tomato Sauce",  brand="Mega",      category=ProductCategory.CANNED_GOODS,   unit="155g",  imageEmoji="🐟"),
        // Instant Noodles
        Product(id=6,  name="Lucky Me Pancit Canton",         brand="Lucky Me",  category=ProductCategory.INSTANT_NOODLES, unit="60g",  imageEmoji="🍜"),
        Product(id=7,  name="Lucky Me Supreme Bulalo",        brand="Lucky Me",  category=ProductCategory.INSTANT_NOODLES, unit="65g",  imageEmoji="🍜"),
        Product(id=8,  name="Payless Instant Mami Chicken",   brand="Payless",   category=ProductCategory.INSTANT_NOODLES, unit="55g",  imageEmoji="🍜"),
        Product(id=9,  name="Nissin Cup Noodles Seafood",     brand="Nissin",    category=ProductCategory.INSTANT_NOODLES, unit="60g",  imageEmoji="🍜"),
        // Beverages
        Product(id=10, name="Nescafe 3-in-1 Original",        brand="Nescafe",   category=ProductCategory.BEVERAGES,      unit="20g sachet", imageEmoji="☕"),
        Product(id=11, name="Kopiko Brown Coffee",             brand="Kopiko",    category=ProductCategory.BEVERAGES,      unit="30g sachet", imageEmoji="☕"),
        Product(id=12, name="Milo Choco Malt Drink",           brand="Milo",      category=ProductCategory.BEVERAGES,      unit="300g",       imageEmoji="🥛"),
        // Condiments & Oil
        Product(id=13, name="Datu Puti Vinegar",               brand="Datu Puti", category=ProductCategory.CONDIMENTS,     unit="385mL", imageEmoji="🧴"),
        Product(id=14, name="Datu Puti Soy Sauce",             brand="Datu Puti", category=ProductCategory.CONDIMENTS,     unit="385mL", imageEmoji="🧴"),
        Product(id=15, name="Minola Coconut Cooking Oil",       brand="Minola",    category=ProductCategory.CONDIMENTS,     unit="350mL", imageEmoji="🫙"),
        Product(id=16, name="UFC Banana Ketchup",              brand="UFC",        category=ProductCategory.CONDIMENTS,     unit="320g",  imageEmoji="🍅"),
        // Personal Care
        Product(id=17, name="Safeguard White Bar Soap",        brand="Safeguard", category=ProductCategory.PERSONAL_CARE,  unit="90g",   imageEmoji="🧼"),
        Product(id=18, name="Head & Shoulders Shampoo",        brand="H&S",       category=ProductCategory.PERSONAL_CARE,  unit="12mL sachet", imageEmoji="🧴"),
        Product(id=19, name="Colgate Toothpaste",              brand="Colgate",   category=ProductCategory.PERSONAL_CARE,  unit="75mL",  imageEmoji="🪥"),
        Product(id=20, name="Joy Dishwashing Liquid",          brand="Joy",       category=ProductCategory.PERSONAL_CARE,  unit="250mL", imageEmoji="🫧")
    )

    // ── PRICE TABLE: (productId, storeId, price, isOnSale) ──────────────────
    //   Store IDs: 1=SM  2=Robinsons  3=Puregold  4=Waltermart  5=AllDay
    val priceEntries: List<PriceEntry> by lazy {
        val rawData = listOf(
            // id, SM,    Rob,  Pure, Walt, AllDay, saleAt (storeId or 0=none)
            Triple(1,  listOf(22.00, 23.00, 20.00, 21.00, 24.00), 3),   // Ligo Sardines — sale at Puregold
            Triple(2,  listOf(55.00, 57.00, 52.00, 54.00, 58.00), 0),
            Triple(3,  listOf(36.00, 37.00, 35.00, 35.00, 38.00), 0),
            Triple(4,  listOf(98.00, 99.00, 95.00, 96.00,102.00), 3),   // Del Monte — sale at Puregold
            Triple(5,  listOf(18.00, 19.00, 17.00, 18.00, 20.00), 0),
            Triple(6,  listOf(14.00, 14.00, 13.00, 13.00, 15.00), 0),
            Triple(7,  listOf(16.00, 16.00, 15.00, 15.00, 17.00), 0),
            Triple(8,  listOf( 8.00,  8.00,  7.00,  8.00,  8.00), 3),
            Triple(9,  listOf(22.00, 23.00, 21.00, 22.00, 24.00), 0),
            Triple(10, listOf( 8.00,  8.00,  7.50,  8.00,  8.50), 3),
            Triple(11, listOf(10.00, 10.00,  9.50,  9.50, 11.00), 4),   // Kopiko — sale at Waltermart
            Triple(12, listOf(88.00, 90.00, 85.00, 87.00, 92.00), 3),   // Milo — sale at Puregold
            Triple(13, listOf(28.00, 29.00, 26.00, 27.00, 30.00), 0),
            Triple(14, listOf(25.00, 25.00, 24.00, 24.00, 26.00), 0),
            Triple(15, listOf(52.00, 54.00, 49.00, 51.00, 55.00), 3),   // Minola — sale at Puregold
            Triple(16, listOf(42.00, 44.00, 40.00, 41.00, 45.00), 0),
            Triple(17, listOf(18.00, 18.00, 17.00, 17.00, 19.00), 0),
            Triple(18, listOf( 8.00,  8.00,  7.00,  8.00,  8.00), 3),
            Triple(19, listOf(58.00, 60.00, 55.00, 57.00, 62.00), 3),   // Colgate — sale at Puregold
            Triple(20, listOf(48.00, 49.00, 45.00, 47.00, 52.00), 0)
        )

        val entries = mutableListOf<PriceEntry>()
        rawData.forEach { (productId, prices, salestoreId) ->
            prices.forEachIndexed { idx, price ->
                val storeId = (idx + 1).toLong()
                val onSale  = (storeId == salestoreId.toLong())
                entries += PriceEntry(
                    productId    = productId.toLong(),
                    storeId      = storeId,
                    price        = price,
                    isOnSale     = onSale,
                    saleLabel    = if (onSale) "SALE" else ""
                )
            }
        }
        entries
    }
}
