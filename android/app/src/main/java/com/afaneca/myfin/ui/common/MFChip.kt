package com.afaneca.myfin.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.afaneca.myfin.ui.theme.LocalSpacing

@Composable
fun MFChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    enabled: Boolean = true,
) {
    AssistChip(
        onClick = onClick,
        modifier = modifier,
        label = { Text(text = label, modifier = Modifier.padding(LocalSpacing.current.small)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = colors,
        enabled = enabled,
    )
}