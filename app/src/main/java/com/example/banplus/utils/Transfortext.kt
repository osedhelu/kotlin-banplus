package com.example.banplus.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.lang.Integer.max

const val mask = "(xxxx) xxx-xxxx"
fun mobileNumberFilter(text: AnnotatedString):
        TransformedText {
//    if (formType != FormType.SHIPPING_PHONE) {
//        return VisualTransformation.None.filter(text)
//    }

    // change the length
    val trimmed =
        if (text.text.length >= 15) text.text.substring(0..13) else text.text

    val annotatedString = AnnotatedString.Builder().run {
        for (i in trimmed.indices) {
            val trimmedPortion = trimmed[i]
            if (i == 0) {
                append("($trimmedPortion")
            } else {
                append(trimmedPortion)
            }
            if (i == 3) {
                append(") ")
            }
            if (i == 6) {
                append("-")
            }
        }
        pushStyle(
            SpanStyle(color = Color.LightGray)
        )
        try {
            append(mask.takeLast(mask.length - length))
        } catch (e: IllegalArgumentException) {
//            Timber.d(e.localizedMessage?.plus(" reached end of phone number"))
        }

        toAnnotatedString()
    }

    val translator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 4) return offset + 1
            if (offset <= 7) return offset + 3
            if (offset <= 11) return offset + 4
            return 15

        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset + 1
            if (offset <= 7) return offset + 3
            if (offset <= 11) return offset + 4
            return 15

        }
    }

    return TransformedText(annotatedString, translator)
}
//
//class MaskTransformation() : VisualTransformation {
//    override fun filter(text: AnnotatedString): TransformedText {
//        return maskFilter(text)
//    }
//}
//
//
//fun maskFilter(text: AnnotatedString): TransformedText {
//
//    // NNNNN-NNN
//    val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
//    var out = ""
//    for (i in trimmed.indices) {
//        out += trimmed[i]
//        if (i==4) out += "-"
//    }
//
//    val numberOffsetTranslator = object : OffsetMapping {
//        override fun originalToTransformed(offset: Int): Int {
//            if (offset <= 4) return offset
//            if (offset <= 8) return offset +1
//            return 9
//
//        }
//
//        override fun transformedToOriginal(offset: Int): Int {
//            if (offset <=5) return offset
//            if (offset <=9) return offset -1
//            return 8
//        }
//    }
//
//    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
//}

class CurrencyAmountInputVisualTransformation(
    private val fixedCursorAtTheEnd: Boolean = true
) : VisualTransformation {

    companion object {
        const val CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS = 2
    }

    private val symbols = DecimalFormat().decimalFormatSymbols
    private val thousandsReplacementPattern = Regex("\\B(?=(?:\\d{3})+(?!\\d))")

    override fun filter(text: AnnotatedString): TransformedText {
        val thousandsSeparator = symbols.groupingSeparator
        val decimalSeparator = symbols.decimalSeparator
        val zero = symbols.zeroDigit

        val inputText = text.text

        val intPart = if (inputText.length > CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS) {
            inputText.subSequence(0, inputText.length - CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS)
        } else {
            zero.toString()
        }
        var fractionPart = if (inputText.length >= CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS) {
            inputText.subSequence(
                inputText.length - CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS,
                inputText.length
            )
        } else {
            inputText
        }

        val formattedIntWithThousandsSeparator =
            intPart.replace(thousandsReplacementPattern, thousandsSeparator.toString())

        if (fractionPart.length < CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS) {
            fractionPart = fractionPart.padStart(CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS, zero)
        }

        val newText = AnnotatedString(
            formattedIntWithThousandsSeparator + decimalSeparator + fractionPart,
            text.spanStyles,
            text.paragraphStyles
        )

        val offsetMapping = if (fixedCursorAtTheEnd) {
            FixedCursorOffsetMapping(
                unmaskedTextLength = intPart.length,
                decimalDigits = CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS
            )
        } else {
            MovableCursorOffsetMapping(
                unmaskedText = text.toString(),
                maskedText = newText.toString(),
                decimalDigits = CURRENCY_AMOUNT_FORMAT_NUMBER_OF_DECIMALS
            )
        }

        return TransformedText(newText, offsetMapping)
    }

    private inner class FixedCursorOffsetMapping(
        private val unmaskedTextLength: Int,
        private val decimalDigits: Int
    ) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            when (offset) {
                0, 1, 2 -> 4
                else -> offset + 1 + calculateThousandsSeparatorCount(unmaskedTextLength)
            }

        override fun transformedToOriginal(offset: Int): Int =
            unmaskedTextLength + calculateThousandsSeparatorCount(unmaskedTextLength) + decimalDigits

        private fun calculateThousandsSeparatorCount(unmaskedTextLength: Int) =
            max((unmaskedTextLength - 1) / 3, 0)
    }

    private inner class MovableCursorOffsetMapping(
        private val unmaskedText: String,
        private val maskedText: String,
        private val decimalDigits: Int
    ) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            when {
                unmaskedText.length <= decimalDigits -> {
                    maskedText.length - (unmaskedText.length - offset)
                }
                else -> {
                    offset + offsetMaskCount(offset, maskedText)
                }
            }

        override fun transformedToOriginal(offset: Int): Int =
            when {
                unmaskedText.length <= decimalDigits -> {
                    max(unmaskedText.length - (maskedText.length - offset), 0)
                }
                else -> {
                    offset - maskedText.take(offset).count { !it.isDigit() }
                }
            }

        private fun offsetMaskCount(offset: Int, maskedText: String): Int {
            var maskOffsetCount = 0
            var dataCount = 0
            for (maskChar in maskedText) {
                if (!maskChar.isDigit()) {
                    maskOffsetCount++
                } else if (++dataCount > offset) {
                    break
                }
            }
            return maskOffsetCount
        }
    }
}


fun addDecimals(e:String): String {
    return when(e.length) {
        1 -> "0.0${e}"
        2 -> "0.${e}"
        else -> "${e.substring(0,e.length - 2)}.${e.substring(e.length - 2)}"

    }
}

