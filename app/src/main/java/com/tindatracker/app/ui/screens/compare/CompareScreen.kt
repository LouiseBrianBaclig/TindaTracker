package com.tindatracker.app.ui.screens.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tindatracker.app.data.model.PriceComparison
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.data.model.Store
import com.tindatracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(viewModel: CompareViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMsg()
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceBackground
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceBackground)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            Surface(color = GreenPrimary, shadowElevation = 4.dp) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "⚖️  Ikumpara ang Presyo",
                            style      = MaterialTheme.typography.titleLarge,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.weight(1f)
                        )
                        if (state.selectedProduct != null) {
                            IconButton(onClick = viewModel::clearSelection) {
                                Icon(Icons.Filled.Close, "I-clear", tint = Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
                return@Scaffold
            }

            if (state.selectedProduct == null) {
                ProductPickerList(
                    products = state.allProducts,
                    onSelect = { viewModel.selectProduct(it) }
                )
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Selected product header
                    val selected = state.allProducts.firstOrNull {
                        it.product.id == state.selectedProduct!!.id
                    }
                    if (selected != null) {
                        item { SelectedProductHeader(product = selected) }
                    }

                    // Savings summary
                    if (state.priceComparisons.size >= 2) {
                        val best  = state.priceComparisons.first()
                        val worst = state.priceComparisons.last()
                        if (worst.price > best.price) {
                            item { SavingsSummaryCard(best = best, worst = worst) }
                        }
                    }

                    // Section header
                    item {
                        Text(
                            "Presyo sa Bawat Tindahan",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Price rows per store
                    items(state.priceComparisons) { comparison ->
                        PriceComparisonRow(comparison = comparison)
                    }

                    // Update price action
                    item {
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(
                            onClick  = viewModel::showUpdateDialog,
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                            border   = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Icon(Icons.Filled.Edit, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("I-update ang Presyo")
                        }
                    }
                }
            }
        }
    }

    // Update price dialog
    if (state.showUpdateDialog && state.selectedProduct != null) {
        UpdatePriceDialog(
            stores    = state.priceComparisons.map { it.store },
            onUpdate  = { storeId, price -> viewModel.updatePrice(storeId, price) },
            onDismiss = viewModel::hideUpdateDialog
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Product picker (shown when no product is selected yet)
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun ProductPickerList(
    products: List<ProductWithBestPrice>,
    onSelect: (Long) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Surface(color = Color.White, shadowElevation = 1.dp) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.TouchApp, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Pumili ng produkto para ikumpara:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        LazyColumn(
            contentPadding      = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products, key = { it.product.id }) { deal ->
                Card(
                    onClick   = { onSelect(deal.product.id) },
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier  = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(deal.product.imageEmoji, fontSize = 26.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                deal.product.name,
                                style      = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${deal.product.brand} · ${deal.product.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "₱ %.2f".format(deal.bestPrice),
                                style      = MaterialTheme.typography.titleMedium,
                                color      = GreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${deal.storeEmoji} ${deal.bestStoreName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Selected product header card
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun SelectedProductHeader(product: ProductWithBestPrice) {
    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = GreenPrimary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(product.product.imageEmoji, fontSize = 40.sp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    product.product.name,
                    style      = MaterialTheme.typography.titleMedium,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${product.product.brand}  ·  ${product.product.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pinakamababa: ₱ %.2f  (${product.bestStoreName})".format(product.bestPrice),
                    style      = MaterialTheme.typography.labelMedium,
                    color      = AmberContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Savings summary banner
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun SavingsSummaryCard(best: PriceComparison, worst: PriceComparison) {
    val saved = worst.price - best.price
    Card(
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = GreenContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("💰", fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Makatitipid ka ng",
                    style = MaterialTheme.typography.labelMedium,
                    color = GreenDark
                )
                Text(
                    "₱ %.2f".format(saved),
                    style      = MaterialTheme.typography.headlineSmall,
                    color      = GreenPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "kung bibilhin sa ${best.store.name} (₱ %.2f) ".format(best.price) +
                    "kaysa ${worst.store.name} (₱ %.2f)".format(worst.price),
                    style = MaterialTheme.typography.labelSmall,
                    color = GreenDark
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Per-store price row
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun PriceComparisonRow(comparison: PriceComparison) {
    val storeColor = hexToColor(comparison.store.colorHex)
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (comparison.isBestPrice) GreenContainer.copy(alpha = 0.7f)
                             else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (comparison.isBestPrice) 4.dp else 1.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(storeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Text(comparison.store.emoji, fontSize = 22.sp) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        comparison.store.name,
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (comparison.isBestPrice) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = GreenPrimary) {
                            Text(
                                "PINAKAMURA",
                                style      = MaterialTheme.typography.labelSmall,
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (comparison.isOnSale) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = SaleRed) {
                            Text(
                                "SALE",
                                style      = MaterialTheme.typography.labelSmall,
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    comparison.store.description,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Text(
                "₱ %.2f".format(comparison.price),
                style      = MaterialTheme.typography.titleMedium,
                color      = if (comparison.isBestPrice) GreenPrimary
                             else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (comparison.isBestPrice) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Update price dialog
// ─────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdatePriceDialog(
    stores   : List<Store>,
    onUpdate : (storeId: Long, price: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStore by remember { mutableStateOf(stores.firstOrNull()) }
    var priceText     by remember { mutableStateOf("") }
    var dropdownOpen  by remember { mutableStateOf(false) }
    val isValid = priceText.toDoubleOrNull()?.let { it > 0 } == true

    AlertDialog(
        onDismissRequest = onDismiss,
        icon  = { Text("✏️", fontSize = 28.sp) },
        title = { Text("I-update ang Presyo", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Pumili ng tindahan at ilagay ang bagong presyo na iyong nakita.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Store dropdown
                ExposedDropdownMenuBox(
                    expanded         = dropdownOpen,
                    onExpandedChange = { dropdownOpen = it }
                ) {
                    OutlinedTextField(
                        value         = selectedStore?.let { "${it.emoji}  ${it.name}" }
                                        ?: "Pumili ng tindahan",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Tindahan") },
                        trailingIcon  = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownOpen)
                        },
                        modifier      = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded         = dropdownOpen,
                        onDismissRequest = { dropdownOpen = false }
                    ) {
                        stores.forEach { store ->
                            DropdownMenuItem(
                                text    = { Text("${store.emoji}  ${store.name}") },
                                onClick = {
                                    selectedStore = store
                                    dropdownOpen  = false
                                }
                            )
                        }
                    }
                }

                // Price input
                OutlinedTextField(
                    value           = priceText,
                    onValueChange   = { priceText = it },
                    label           = { Text("Bagong Presyo") },
                    prefix          = { Text("₱ ") },
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier        = Modifier.fillMaxWidth(),
                    isError         = priceText.isNotEmpty() && !isValid,
                    supportingText  = {
                        if (priceText.isNotEmpty() && !isValid)
                            Text("Ilagay ang tamang halaga.")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    val store = selectedStore ?: return@Button
                    val price = priceText.toDoubleOrNull() ?: return@Button
                    onUpdate(store.id, price)
                },
                enabled  = isValid && selectedStore != null,
                colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) { Text("I-save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Kanselahin") }
        }
    )
}
