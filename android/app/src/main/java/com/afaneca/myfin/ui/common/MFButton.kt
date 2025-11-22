package com.afaneca.myfin.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.afaneca.myfin.ui.theme.LocalSpacing
import androidx.compose.ui.tooling.preview.Preview
import com.afaneca.myfin.ui.theme.AppTheme

@Composable
private fun onDebounceClick(
    onClick: () -> Unit,
    debounceTimeMillis: Long = 500L,
): () -> Unit {
    var lastClickTimeMillis: Long by remember { mutableLongStateOf(value = 0L) }
    return {
        System.currentTimeMillis().let { currentTimeMillis ->
            if((currentTimeMillis - lastClickTimeMillis) >= debounceTimeMillis) {
                lastClickTimeMillis = currentTimeMillis
                onClick()
            }
        }
    }
}

@Composable
fun MFPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalSpacing.current.larger3),
        shape = MaterialTheme.shapes.large,
        enabled = isEnabled,
        onClick = onDebounceClick(
            onClick = onClick,
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
fun MFPrimaryButtonPreview() {
    AppTheme {
        MFPrimaryButton(text = "My Button") {}
    }
}