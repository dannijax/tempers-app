package chapters.main.reg.mvp

import android.app.Activity
import chapters.extension.dateWithFlash
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*


class PresenterReg(val mvpReg: MvpReg) {

    private var dateBirthday: Calendar? = null

    fun openCalendar(ac: Activity) {
        val min = Calendar.getInstance()
        min.add(Calendar.YEAR,-13)
        val dpd = DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            dateBirthday = Calendar.getInstance()
            dateBirthday?.set(Calendar.YEAR, year)
            dateBirthday?.set(Calendar.MONTH, monthOfYear)
            dateBirthday?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mvpReg.setDate(dateBirthday?.time?.time?.dateWithFlash())

        },
                min.get(Calendar.YEAR),
                min.get(Calendar.MONTH),
                min.get(Calendar.DAY_OF_MONTH))

        dpd.maxDate = min
        dpd.show(ac.fragmentManager, "Datepickerdialog")
    }
}