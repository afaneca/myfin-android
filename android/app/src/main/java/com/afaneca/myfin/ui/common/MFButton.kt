package com.afaneca.myfin.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing

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

class MFSecondaryButton {
    companion object {
        @Composable
        operator fun invoke(
            text: String,
            modifier: Modifier = Modifier,
            isEnabled: Boolean = true,
            color: Color = MaterialTheme.colorScheme.primary,
            onClick: () -> Unit,
        ) {
            OutlinedButton(
                modifier = modifier
                    .fillMaxWidth()
                    .height(LocalSpacing.current.larger3), // Corrected to use larger2
                shape = MaterialTheme.shapes.large,
                enabled = isEnabled,
                onClick = onDebounceClick(
                    onClick = onClick,
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                border = BorderStroke(width = 1.dp, color = color)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        @Composable
        fun Negative(
            text: String,
            modifier: Modifier = Modifier,
            isEnabled: Boolean = true,
            onClick: () -> Unit,
        ) {
            invoke(
                text = text,
                modifier = modifier,
                isEnabled = isEnabled,
                color = MaterialTheme.colorScheme.error,
                onClick = onClick,
            )
        }
    }
}

@Preview
@Composable
fun MFButtonPreview() {
    AppTheme {
        Column {
            MFPrimaryButton(text = "My Button") {}
            MFSecondaryButton(text = "My Secondary Button") {}
            MFSecondaryButton.Negative(text = "My Negative Button") {}
        }
    }
}
