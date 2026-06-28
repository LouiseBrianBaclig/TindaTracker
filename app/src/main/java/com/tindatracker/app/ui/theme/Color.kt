package com.tindatracker.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── TindaTracker brand palette ────────────────────────────────────────────
val GreenPrimary        = Color(0xFF1B8A5A)   // deep sari-sari green
val GreenDark           = Color(0xFF136640)
val GreenLight          = Color(0xFF4DB87E)
val GreenContainer      = Color(0xFFBCEDD5)
val OnGreenContainer    = Color(0xFF002112)

val AmberPrimary        = Color(0xFFFF8F00)   // warm amber accent
val AmberContainer      = Color(0xFFFFE082)
val OnAmberContainer    = Color(0xFF2C1600)

val SaleRed             = Color(0xFFC62828)
val PriceGreenText      = Color(0xFF1B8A5A)
val StarGold            = Color(0xFFFFB300)

val SurfaceBackground   = Color(0xFFF3FAF6)   // very light mint
val CardSurface         = Color(0xFFFFFFFF)
val DividerColor        = Color(0xFFE0E0E0)

// ── Store brand colours ───────────────────────────────────────────────────
val SMBlue              = Color(0xFF1565C0)
val RobinsonsOrange     = Color(0xFFE65100)
val PuregoldRed         = Color(0xFFC62828)
val WaltermartPurple    = Color(0xFF4527A0)
val AllDayGreen         = Color(0xFF2E7D32)

/** Parse a hex string like "#1565C0" to a Compose Color. */
fun hexToColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) { Color.Gray }
