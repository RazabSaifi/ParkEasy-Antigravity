package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NotificationItem
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

@Composable
fun NotificationDialog(
    notifications: List<NotificationItem>,
    onMarkRead: (Long) -> Unit,
    onMarkAllRead: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .testTag("notification_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                if (notifications.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onMarkAllRead) {
                            Text("Mark all read", fontSize = 12.sp, color = PrimaryBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (notifications.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    ) {
                        Text(
                            text = "You're all caught up! No notifications.",
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        items(notifications) { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMarkRead(notif.id) }
                                    .background(if (!notif.isRead) Color(0xFFEFF6FF) else Color.Transparent)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (notif.type) {
                                                "Earnings" -> Color(0xFFDCFCE7)
                                                "Review" -> Color(0xFFFEF3C7)
                                                "Listing" -> Color(0xFFEDE9FE)
                                                else -> Color(0xFFEFF6FF)
                                            }
                                        )
                                ) {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "Earnings" -> Icons.Default.Payment
                                            "Review" -> Icons.Default.Star
                                            "Listing" -> Icons.Default.LocalOffer
                                            else -> Icons.Default.CheckCircle
                                        },
                                        contentDescription = null,
                                        tint = when (notif.type) {
                                            "Earnings" -> AccentEmerald
                                            "Review" -> Color(0xFFD97706)
                                            "Listing" -> Color(0xFF7C3AED)
                                            else -> PrimaryBlue
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = Slate900
                                        )
                                        if (!notif.isRead) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryBlue)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        fontSize = 12.sp,
                                        color = Slate500,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            HorizontalDivider(color = Slate100)
                        }
                    }
                }
            }
        }
    }
}
