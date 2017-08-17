package chapters.ui.uiUtils

import android.text.InputFilter
import android.text.InputType
import android.widget.TextView


fun TextView.filterEditText(str: String, maxLenght: Int = 99, inputType: Int = InputType.TYPE_CLASS_TEXT) {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start..end - 1) {
            if (!str.contains(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }
    val fil = InputFilter.LengthFilter(maxLenght)
    this.filters = arrayOf(fil, filter)
    this.inputType = inputType
}

fun TextView.changeMaxLenth(lenght: Int) {
    this.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(lenght))
    if (this.text.length > lenght){
        this.text=""
    }
}