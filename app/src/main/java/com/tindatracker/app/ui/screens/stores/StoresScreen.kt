package com.tindatracker.app.ui.screens.stores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tindatracker.app.ui.theme.*

@Composable
fun StoresScreen(viewModel: StoresViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Surface(color = GreenPrimary, shadowElevation = 4.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "🏪  Mga Tindahan",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(vertical = 12.dp)
                )
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
            return
        }

        LazyColumn(
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                InfoBanner()
            }

            items(state.stores, key = { it.store.id }) { detail ->
                StoreCard(detail = detail)
            }

            item {
                DisclaimerCard()
            }
        }
    }
}

@Composable
private fun InfoBanner() {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(20.dp).padding(top = 2.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Ang mga presyo ay base sa datos na nakolekta mula sa iba't ibang " +
                "supermarket sa Pilipinas. Pinunan bawat linggo para sa pinaka-tumpak na " +
                "impormasyon. I-tap ang \"I-update ang Presyo\" sa Ikumpara kung may bago kang nakita.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1A237E)
            )
        }
    }
}

@Composable
private fun StoreCard(detail: StoreDetail) {
    val store      = detail.store
    val storeColor = hexToColor(store.colorHex)

    Card(
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column {
            // Gradient header strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(storeColor, storeColor.copy(alpha = 0.4f))
                        )
                    )
            )

            Column(Modifier.padding(16.dp)) {
                // Store name row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(storeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(store.emoji, fontSize = 26.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            store.name,
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            store.description,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = storeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            store.shortName,
                            style      = MaterialTheme.typography.labelMedium,
                            color      = storeColor,
                            fontWeight = FontWeight.ExtraBold,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(14.dp))

                // Stats grid
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        modifier = Modifier.weight(1f),
                        label    = "Produkto",
                        value    = "${detail.productCount}",
                        icon     = "📦",
                        color    = storeColor
                    )
                    StatBox(
                        modifier = Modifier.weight(1f),
                        label    = "Pinakamababa",
                        value    = "₱ %.2f".format(detail.minPrice),
                        icon     = "⬇️",
                        color    = storeColor
                    )
                    StatBox(
                        modifier = Modifier.weight(1f),
                        label    = "Pinakamataas",
                        value    = "₱ %.2f".format(detail.maxPrice),
                        icon     = "⬆️",
                        color    = storeColor
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Best-deal highlight
                Surface(
                    shape    = RoundedCornerShape(10.dp),
                    color    = storeColor.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocalOffer,
                            contentDescription = null,
                            tint     = storeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pinaka-mababang presyo sa ${detail.productCount} produkto.",
                            style = MaterialTheme.typography.bodySmall,
                            color = storeColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    modifier: Modifier,
    label   : String,
    value   : String,
    icon    : String,
    color   : Color
) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(10.dp),
        color    = SurfaceBackground
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color      = color,
                maxLines   = 1
            )
            Text(
                label,
                style   = MaterialTheme.typography.labelSmall,
                color   = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DisclaimerCard() {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Text("⚠️", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Paalala",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ang mga presyo ay maaaring magbago anumang oras. " +
                    "Laging i-verify ang aktwal na presyo sa tindahan bago bumili ng malaking dami. " +
                    "Ang TindaTracker ay para lamang sa gabay at pagpaplano ng pamimili.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6D4C41)
                )
            }
        }
    }
}
