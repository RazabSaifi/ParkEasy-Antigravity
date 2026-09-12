package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.example.ui.i18n.LocalStrings
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
    val strings = LocalStrings.current

    val items = if (activeMode == "Provider") {
        listOf(
            NavItem(AppScreen.PROVIDER_DASHBOARD, strings.navHome, Icons.Default.Home, "nav_provider_dashboard"),
            NavItem(AppScreen.MY_SPACES, strings.navSpaces, Icons.Default.LocalParking, "nav_provider_spaces"),
            NavItem(AppScreen.MY_BOOKINGS, strings.navBookings, Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.EARNINGS, strings.navEarnings, Icons.Default.AccountBalanceWallet, "nav_provider_earnings"),
            NavItem(AppScreen.PROFILE, strings.navProfile, Icons.Default.Person, "nav_profile")
        )
    } else if (activeMode == "Admin") {
        listOf(
            NavItem(AppScreen.ADMIN_DASHBOARD, strings.navOverview, Icons.Default.Dashboard, "nav_admin_dashboard"),
            NavItem(AppScreen.EXPLORE, strings.navExplore, Icons.Default.Explore, "nav_explore"),
            NavItem(AppScreen.MY_BOOKINGS, strings.navBookings, Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.PROFILE, strings.navProfile, Icons.Default.Person, "nav_profile")
        )
    } else {
        listOf(
            NavItem(AppScreen.HOME, strings.navHome, Icons.Default.Home, "nav_home"),
            NavItem(AppScreen.EXPLORE, strings.navExplore, Icons.Default.Explore, "nav_explore"),
            NavItem(AppScreen.MY_BOOKINGS, strings.navBookings, Icons.Default.EventNote, "nav_bookings"),
            NavItem(AppScreen.LIST_SPACE_WIZARD, strings.navListSpace, Icons.Default.AddBox, "nav_list_space"),
            NavItem(AppScreen.PROFILE, strings.navProfile, Icons.Default.Person, "nav_profile")
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
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
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
