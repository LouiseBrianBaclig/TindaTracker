package com.tindatracker.app.ui.screens.shopping

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tindatracker.app.data.model.ProductWithBestPrice
import com.tindatracker.app.data.model.ShoppingListWithDetails
import com.tindatracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = SurfaceBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::showAddDialog,
                containerColor = GreenPrimary,
                contentColor   = Color.White,
                shape          = CircleShape
            ) { Icon(Icons.Filled.Add, "Magdagdag") }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceBackground)
        ) {

            // ── Top bar ──────────────────────────────────────────────────────
            Surface(color = GreenPrimary, shadowElevation = 4.dp) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "🛒  Listahan ng Pamili",
                            style      = MaterialTheme.typography.titleLarge,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.weight(1f)
                        )
                        if (state.checkedCount > 0) {
                            TextButton(onClick = viewModel::clearChecked) {
                                Text(
                                    "Alisin (${state.checkedCount})",
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }

                    // Running total banner
                    if (state.listItems.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            shape  = RoundedCornerShape(10.dp),
                            color  = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Kabuuang Halaga",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.8f))
                                    Text(
                                        "₱ %.2f".format(state.totalCost),
                                        style      = MaterialTheme.typography.headlineSmall,
                                        color      = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${state.listItems.size} produkto",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f))
                                    Text("${state.checkedCount} tapos na",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberContainer)
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // ── Body ─────────────────────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }
                state.listItems.isEmpty() -> {
                    EmptyListPlaceholder(onAdd = viewModel::showAddDialog)
                }
                else -> {
                    LazyColumn(
                        contentPadding      = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier            = Modifier.fillMaxSize()
                    ) {
                        val unchecked = state.listItems.filter { !it.item.isChecked }
                        val checked   = state.listItems.filter {  it.item.isChecked }

                        if (unchecked.isNotEmpty()) {
                            item {
                                Text("Para bilhin (${unchecked.size})",
                                    style    = MaterialTheme.typography.labelMedium,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                            }
                            items(unchecked, key = { it.item.id }) { detail ->
                                ShoppingItemRow(
                                    detail      = detail,
                                    onCheck     = { viewModel.toggleChecked(detail.item) },
                                    onIncrement = { viewModel.incrementQty(detail.item) },
                                    onDecrement = { viewModel.decrementQty(detail.item) },
                                    onRemove    = { viewModel.removeItem(detail.item.id) }
                                )
                            }
                        }

                        if (checked.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(4.dp))
                                Text("Tapos na (${checked.size})",
                                    style    = MaterialTheme.typography.labelMedium,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                            }
                            items(checked, key = { it.item.id }) { detail ->
                                ShoppingItemRow(
                                    detail      = detail,
                                    onCheck     = { viewModel.toggleChecked(detail.item) },
                                    onIncrement = { viewModel.incrementQty(detail.item) },
                                    onDecrement = { viewModel.decrementQty(detail.item) },
                                    onRemove    = { viewModel.removeItem(detail.item.id) }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddProductDialog(
            products  = state.allProducts,
            existing  = state.listItems.map { it.product.id }.toSet(),
            onAdd     = { productId, qty -> viewModel.addProduct(productId, qty) },
            onDismiss = viewModel::hideAddDialog
        )
    }
}

// ── Shopping item row ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingItemRow(
    detail     : ShoppingListWithDetails,
    onCheck    : () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove   : () -> Unit
) {
    val item    = detail.item
    val product = detail.product
    val checked = item.isChecked

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onRemove(); true } else false
        }
    )

    SwipeToDismissBox(
        state                    = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SaleRed),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, "Tanggalin",
                    tint     = Color.White,
                    modifier = Modifier.padding(end = 20.dp))
            }
        }
    ) {
        Card(
            shape  = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (checked) Color(0xFFF5F5F5) else Color.White
            ),
            elevation = CardDefaults.cardElevation(if (checked) 0.dp else 2.dp),
            modifier  = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked         = checked,
                    onCheckedChange = { onCheck() },
                    colors          = CheckboxDefaults.colors(
                        checkedColor   = GreenPrimary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Text(product.imageEmoji, fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 6.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text           = product.name,
                        style          = MaterialTheme.typography.labelLarge,
                        fontWeight     = FontWeight.SemiBold,
                        maxLines       = 1,
                        overflow       = TextOverflow.Ellipsis,
                        textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                        color          = if (checked) MaterialTheme.colorScheme.onSurfaceVariant
                                         else MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("₱ %.2f".format(detail.bestPrice),
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                        Text(" · ${detail.bestStoreName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (item.quantity > 1) {
                        Text("Subtotal: ₱ %.2f".format(detail.lineTotal),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberPrimary, fontWeight = FontWeight.Medium)
                    }
                }
                if (!checked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Remove, "Bawasan",
                                tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text("${item.quantity}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.widthIn(min = 24.dp))
                        IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Add, "Dagdagan",
                                tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────

@Composable
private fun EmptyListPlaceholder(onAdd: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🛒", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("Walang laman ang iyong lista.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text("I-tap ang + para magdagdag ng produkto.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
            Icon(Icons.Filled.Add, null)
            Spacer(Modifier.width(6.dp))
            Text("Magdagdag ng Produkto")
        }
    }
}

// ── Add product dialog ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductDialog(
    products : List<ProductWithBestPrice>,
    existing : Set<Long>,
    onAdd    : (productId: Long, quantity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var searchText   by remember { mutableStateOf("") }
    var selectedDeal by remember { mutableStateOf<ProductWithBestPrice?>(null) }
    var quantity     by remember { mutableIntStateOf(1) }

    val filtered = products.filter {
        it.product.name.contains(searchText, ignoreCase = true) ||
        it.product.brand.contains(searchText, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon  = { Text("➕", fontSize = 28.sp) },
        title = { Text("Magdagdag sa Lista", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Search field
                OutlinedTextField(
                    value         = searchText,
                    onValueChange = { searchText = it; selectedDeal = null },
                    label         = { Text("Hanapin ang produkto") },
                    leadingIcon   = { Icon(Icons.Filled.Search, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )

                // Product list
                if (selectedDeal == null) {
                    Surface(
                        shape  = RoundedCornerShape(8.dp),
                        color  = SurfaceBackground,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                    ) {
                        LazyColumn {
                            items(filtered.take(30), key = { it.product.id }) { deal ->
                                val alreadyIn = deal.product.id in existing
                                ListItem(
                                    headlineContent = {
                                        Text("${deal.product.imageEmoji}  ${deal.product.name}",
                                            style    = MaterialTheme.typography.labelLarge,
                                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    supportingContent = {
                                        Text("₱ %.2f · ${deal.bestStoreName}".format(deal.bestPrice),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (alreadyIn) MaterialTheme.colorScheme.onSurfaceVariant
                                                    else GreenPrimary)
                                    },
                                    trailingContent = {
                                        if (alreadyIn)
                                            Text("✔", color = GreenPrimary, fontWeight = FontWeight.Bold)
                                    },
                                    modifier = if (!alreadyIn)
                                        Modifier.clickable { selectedDeal = deal; searchText = deal.product.name; quantity = 1 }
                                    else Modifier
                                )
                                HorizontalDivider(color = DividerColor)
                            }
                        }
                    }
                }

                // Selected product confirmation
                if (selectedDeal != null) {
                    Surface(shape = RoundedCornerShape(10.dp), color = GreenContainer) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedDeal!!.product.imageEmoji, fontSize = 26.sp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(selectedDeal!!.product.name,
                                    style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                Text("₱ %.2f · ${selectedDeal!!.bestStoreName}".format(selectedDeal!!.bestPrice),
                                    style = MaterialTheme.typography.labelSmall, color = GreenDark)
                            }
                        }
                    }

                    // Quantity stepper
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Dami:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(12.dp))
                        IconButton(
                            onClick  = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(GreenContainer)
                        ) { Icon(Icons.Filled.Remove, "Bawasan", tint = GreenPrimary) }
                        Text(
                            " $quantity ",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.widthIn(min = 44.dp)
                        )
                        IconButton(
                            onClick  = { quantity++ },
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(GreenContainer)
                        ) { Icon(Icons.Filled.Add, "Dagdagan", tint = GreenPrimary) }
                    }
                    Text(
                        "Subtotal: ₱ %.2f".format(selectedDeal!!.bestPrice * quantity),
                        style      = MaterialTheme.typography.bodyMedium,
                        color      = GreenPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { onAdd(selectedDeal!!.product.id, quantity) },
                enabled  = selectedDeal != null,
                colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Idagdag")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Kanselahin") }
        }
    )
}
