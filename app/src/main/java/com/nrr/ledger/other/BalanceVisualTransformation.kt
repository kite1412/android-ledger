package com.nrr.ledger.other

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.nrr.ledger.util.formatBalance

class BalanceVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val mText = text.text
        var adjustedString = mText
        var offsetMapping = OffsetMapping.Identity
        if (mText.isNotEmpty()) {
            try {
                val int = mText.toInt()
                adjustedString = formatBalance(int)
                if (int in 1000..9999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 1
                        override fun transformedToOriginal(offset: Int): Int = offset - 1
                    }
                }
                if (int in 10000..99999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 1
                        override fun transformedToOriginal(offset: Int): Int = offset - 1
                    }
                }
                if (int in 100000..999999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 1
                        override fun transformedToOriginal(offset: Int): Int = offset - 1
                    }
                }
                if (int in 1000000..9999999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 2
                        override fun transformedToOriginal(offset: Int): Int = offset - 2
                    }
                }
                if (int in 10000000..99999999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 2
                        override fun transformedToOriginal(offset: Int): Int = offset - 2
                    }
                }
                if (int in 100000000..999999999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 2
                        override fun transformedToOriginal(offset: Int): Int = offset - 2
                    }
                }
                if (int in 1000000000..9999999999) {
                    offsetMapping = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset + 3
                        override fun transformedToOriginal(offset: Int): Int = offset - 3
                    }
                }
            } catch (_: Throwable) {}
        }
        return TransformedText(
            AnnotatedString(adjustedString),
            offsetMapping
        )
    }
}