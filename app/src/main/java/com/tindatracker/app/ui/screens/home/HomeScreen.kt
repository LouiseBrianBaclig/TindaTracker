package com.tindatracker.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.data.model.Store
import com.tindatracker.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenPrimary)
        }
        return
    }

    LazyColumn(
        modifier       = Modifier.fillMaxSize().background(SurfaceBackground),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { HeaderBanner() }
        item {
            StatsRow(
                storeCount   = state.stores.size,
                productCount = state.productCount
            )
        }

        if (state.bestDeals.isNotEmpty()) {
            item {
                SectionTitle(
                    icon     = Icons.Filled.LocalOffer,
                    title    = "Pinakamababang Presyo Ngayon",
                    subtitle = "Dito ka makakatipid!"
                )
            }
            item {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.bestDeals) { deal -> BestDealCard(deal = deal) }
                }
            }
        }

        item {
            SectionTitle(
                icon     = Icons.Filled.Storefront,
                title    = "Mga Supermarket na Sinusubaybayan",
                subtitle = "${state.stores.size} tindahan"
            )
        }
        item {
            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.stores.forEach { store -> StoreRowCard(store = store) }
            }
        }

        item { TipCard() }
    }
}

// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun HeaderBanner() {
    val hour     = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Magandang Umaga! ☀️"
        hour < 17 -> "Magandang Hapon! 🌤"
        else      -> "Magandang Gabi! 🌙"
    }
    val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).format(Date())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(GreenPrimary, GreenDark)))
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🛒", fontSize = 32.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    "TindaTracker",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(greeting,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f))
            Text(dateStr,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f))
            Spacer(Modifier.height(12.dp))
            Text(
                "Hanapin ang pinakamababang presyo para sa inyong tindahan.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun StatsRow(storeCount: Int, productCount: Int) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip(Modifier.weight(1f), "🏪", "$storeCount",   "Tindahan")
        StatChip(Modifier.weight(1f), "📦", "$productCount", "Produkto")
        StatChip(Modifier.weight(1f), "💰", "Araw-araw",     "Na-update")
    }
}

@Composable
private fun StatChip(modifier: Modifier, emoji: String, value: String, label: String) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(value,
                style      = MaterialTheme.typography.titleMedium,
                color      = GreenPrimary,
                fontWeight = FontWeight.Bold)
            Text(label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String, subtitle: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title,    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BestDealCard(deal: ProductWithBestPrice) {
    Card(
        modifier  = Modifier.width(160.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(deal.product.imageEmoji, fontSize = 30.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                deal.product.name,
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 2,
                lineHeight = 18.sp
            )
            Text(
                deal.product.unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "₱ %.2f".format(deal.bestPrice),
                style      = MaterialTheme.typography.titleMedium,
                color      = GreenPrimary,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(deal.storeEmoji, fontSize = 12.sp)
                Spacer(Modifier.width(3.dp))
                Text(
                    deal.bestStoreName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (deal.hasPriceVariation) {
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = GreenContainer) {
                    Text(
                        "Tipid ₱%.2f".format(deal.savings),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = GreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreRowCard(store: Store) {
    val storeColor = hexToColor(store.colorHex)
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(storeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Text(store.emoji, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
                Text(store.description,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1)
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = storeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    store.shortName,
                    style      = MaterialTheme.typography.labelMedium,
                    color      = storeColor,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TipCard() {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AmberContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Text("💡", fontSize = 24.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Tip ng Araw",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF5D4037))
                Spacer(Modifier.height(4.dp))
                Text(
                    "Bumili ng mas malaking dami sa Puregold o Waltermart para makatipid. " +
                    "Gamitin ang tab na \"Ikumpara\" para makita ang pinakamababang presyo " +
                    "bago pumunta sa supermarket!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6D4C41)
                )
            }
        }
    }
}
