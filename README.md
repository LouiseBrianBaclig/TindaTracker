# 🛒 TindaTracker

**Ang tamang presyo para sa inyong tindahan.**  
*(The right price for your store.)*

A Kotlin + Jetpack Compose Android app that helps **sari-sari store owners** in the Philippines compare prices across the five largest supermarket chains so they can stock up at the lowest possible cost.

---

## ✨ Features

| Screen | Description |
|--------|-------------|
| 🏠 **Home** | Greeting banner, best-deal cards, and quick store overview |
| 📦 **Produkto** | Browse & search 20 products with live best-price badges; category filter chips |
| ⚖️ **Ikumpara** | Select any product → full price table ranked cheapest to most expensive; savings callout; user-editable prices |
| 🛒 **Lista** | Shopping list with quantity steppers, running total, swipe-to-delete, and per-item best-store hint |
| 🏪 **Tindahan** | Store cards with colour branding, product count, and price-range stats |

---

## 🏗 Architecture

```
TindaTracker/
├── data/
│   ├── model/Models.kt          ← Room entities + UI data classes
│   ├── db/AppDatabase.kt        ← RoomDatabase + 4 DAOs
│   ├── repository/PriceRepository.kt
│   └── SampleData.kt            ← 5 stores · 20 products · 100 prices
└── ui/
    ├── theme/                   ← Material 3 colour scheme, typography
    ├── navigation/              ← Screen sealed class, AppNavigation
    └── screens/
        ├── home/                ← HomeViewModel + HomeScreen
        ├── products/            ← ProductsViewModel + ProductsScreen
        ├── compare/             ← CompareViewModel (Activity-scoped) + CompareScreen
        ├── shopping/            ← ShoppingListViewModel + ShoppingListScreen
        └── stores/              ← StoresViewModel + StoresScreen
```

**Pattern:** MVVM · Repository · Room + Flow · Jetpack Compose · Navigation Component

---

## 🏪 Supermarkets Covered

| Store | Short | Colour |
|-------|-------|--------|
| SM Supermarket | SM | 🔵 Blue |
| Robinsons Supermarket | Robinsons | 🟠 Orange |
| Puregold | Puregold | 🔴 Red |
| Waltermart | Waltermart | 🟣 Purple |
| AllDay Supermarket | AllDay | 🟢 Green |

---

## 🛠 Tech Stack

| Layer | Library | Version |
|-------|---------|---------|
| Language | Kotlin | 2.1.0 |
| UI | Jetpack Compose (BOM) | 2025.04.00 |
| Navigation | Navigation Compose | 2.8.5 |
| Database | Room | 2.7.0 |
| Annotation proc. | KSP | 2.1.0-1.0.29 |
| Async | Kotlin Coroutines | 1.9.0 |
| ViewModel | Lifecycle ViewModel | 2.9.0 |
| Build | AGP | 8.9.0 |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | Android 15 | API 35 |

---

## 🚀 Setup

### Prerequisites
- **Android Studio Quail 1 (2026.1.1) Patch 2** or later
- JDK 11+
- Android SDK API 35

### Steps

1. **Clone / open the project**
   ```bash
   # If cloning from Git
   git clone <repo-url>
   cd TindaTracker
   ```
   Or: *File → Open* in Android Studio and select the `TindaTracker/` folder.

2. **Sync Gradle**  
   Android Studio will prompt: **Sync Now** → click it.  
   All dependencies are fetched from Maven Central / Google Maven.

3. **Run on device or emulator**
   - Select a device running API 24+
   - Press **▶ Run** (or `Shift + F10`)

4. **First launch**  
   On first launch, `TindaTrackerApplication.onCreate()` seeds the Room database with:
   - 5 stores
   - 20 products across 5 categories
   - 100 price entries (one per product–store pair)

   This runs once in a background coroutine — the UI renders immediately.

---

## 📱 Screen Walkthroughs

### Home
- Time-aware greeting in Tagalog (Magandang Umaga/Hapon/Gabi)
- Horizontal scrollable **Best Deals** cards showing the 8 biggest savings
- Stats row: stores tracked, products tracked
- Store list with brand colours

### Produkto
- Search bar with real-time debounced filtering
- Category chips: Lahat · Canned Goods · Instant Noodles · Beverages · Condiments & Oil · Personal Care
- 2-column grid with best price, store, and savings badge
- **"Ikumpara"** button on each card navigates to the Compare tab with that product pre-selected

### Ikumpara
- If no product selected → scrollable product picker
- Once selected → colour-coded store rows sorted cheapest first
  - **PINAKAMURA** badge on the cheapest store
  - **SALE** badge for on-sale items
  - Savings summary card showing exact peso savings vs most expensive
- **"I-update ang Presyo"** → dialog to enter a new price you found in-store (writes to Room, updates all views reactively)

### Lista
- Floating **+** button → product search dialog with quantity stepper
- **Swipe left** on any item to delete it
- Tap **checkbox** to mark as done (item moves to "Tapos na" section)
- Running total updates instantly as quantities change
- "Alisin" button removes all checked items

### Tindahan
- Card per store with brand-colour header stripe
- Stats: product count, min/max price tracked
- Info and disclaimer banners

---

## 🌍 Localisation

The app uses **Taglish** (Tagalog–English mix) throughout the UI — the natural language for Filipino sari-sari store owners:

- Navigation labels in Tagalog (`Produkto`, `Ikumpara`, `Lista`, `Tindahan`)
- Greetings, tips, and confirmations in Tagalog
- Prices in Philippine Peso (`₱`)
- Technical/product names in English

---

## 🔮 Future Roadmap

- [ ] Barcode scanner to look up products
- [ ] Cloud sync so multiple store owners can share price updates
- [ ] Price history chart per product
- [ ] Push notifications for price drops
- [ ] Offline-first with background sync when connectivity returns
- [ ] Dark mode
- [ ] Widget for home screen showing today's top deals

---

## 📄 License

MIT — free for personal and commercial use.
