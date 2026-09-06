package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun ParkSpaceBottomNav(
    activeMode: String,
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (activeMode == "Provider") {
        listOf(
            NavItem(AppScreen.PROVIDER_DASHBOARD, "Home", Icons.Default.Home, "nav_provider_dashboard"),
            NavItem(AppScreen.MY_SPACES, "Spaces", Icons.Default.LocalParking, "nav_provider_spaces"),
            NavItem(AppScreen.MY_BOOKINGS, "Bookings", Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.EARNINGS, "Earnings", Icons.Default.AccountBalanceWallet, "nav_provider_earnings"),
            NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
        )
    } else if (activeMode == "Admin") {
        listOf(
            NavItem(AppScreen.ADMIN_DASHBOARD, "Overview", Icons.Default.Dashboard, "nav_admin_dashboard"),
            NavItem(AppScreen.EXPLORE, "All Spaces", Icons.Default.Explore, "nav_explore"),
            NavItem(AppScreen.MY_BOOKINGS, "Bookings", Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
        )
    } else {
        listOf(
            NavItem(AppScreen.HOME, "Home", Icons.Default.Home, "nav_home"),
            NavItem(AppScreen.EXPLORE, "Explore", Icons.Default.Explore, "nav_explore"),
            NavItem(AppScreen.MY_BOOKINGS, "Bookings", Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.LIST_SPACE_WIZARD, "List Space", Icons.Default.AddBox, "nav_list_space"),
            NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
        )
    }

    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            items.forEach { item ->
                val selected = currentScreen == item.screen
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
