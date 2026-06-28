package com.tindatracker.app.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tindatracker.app.data.model.ProductCategory
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = viewModel(),
    onNavigateToCompare: (Long) -> Unit
) {
    val state    by viewModel.uiState.collectAsStateWithLifecycle()
    val query    by viewModel.searchQuery.collectAsStateWithLifecycle()
    val category by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        // Top bar
        Surface(
            color     = GreenPrimary,
            shadowElevation = 4.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📦  Mga Produkto",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${state.products.size} items",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Search field
                OutlinedTextField(
                    value          = query,
                    onValueChange  = viewModel::setSearchQuery,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    placeholder    = { Text("Hanapin ang produkto…") },
                    leadingIcon    = { Icon(Icons.Filled.Search, null) },
                    trailingIcon   = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, "Burahin")
                            }
                        }
                    },
                    singleLine     = true,
                    colors         = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor      = GreenLight,
                        unfocusedBorderColor    = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape           = RoundedCornerShape(12.dp)
                )
            }
        }

        // Category filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProductCategory.all) { cat ->
                val selected = (cat == category)
                FilterChip(
                    selected  = selected,
                    onClick   = { viewModel.setCategory(cat) },
                    label     = {
                        Text(
                            "${ProductCategory.emoji(cat)}  $cat",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors    = FilterChipDefaults.filterChipColors(
                        selectedContainerColor      = GreenPrimary,
                        selectedLabelColor          = Color.White,
                        selectedLeadingIconColor    = Color.White
                    )
                )
            }
        }

        // Product grid
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else if (state.products.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Walang nahanap na produkto.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                contentPadding        = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                items(state.products, key = { it.product.id }) { deal ->
                    ProductCard(
                        deal             = deal,
                        onCompareClick   = { onNavigateToCompare(deal.product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    deal: ProductWithBestPrice,
    onCompareClick: () -> Unit
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            // Emoji + category tag
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(deal.product.imageEmoji, fontSize = 32.sp)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenContainer
                ) {
                    Text(
                        text     = ProductCategory.emoji(deal.product.category),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Name & unit
            Text(
                text       = deal.product.name,
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 2,
                lineHeight = 18.sp
            )
            Text(
                text  = "${deal.product.brand} · ${deal.product.unit}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            // Best price
            Text(
                text  = "₱ %.2f".format(deal.bestPrice),
                style = MaterialTheme.typography.titleMedium,
                color = GreenPrimary,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text  = deal.bestStoreName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Savings badge
            if (deal.hasPriceVariation) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AmberContainer
                ) {
                    Text(
                        "Tipid ₱%.2f".format(deal.savings),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color(0xFF6D4C41),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Compare button
            OutlinedButton(
                onClick  = onCompareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary)
            ) {
                Icon(Icons.Filled.CompareArrows, null, Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Ikumpara", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
