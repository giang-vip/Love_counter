package com.example.giao_dien.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.example.giao_dien.R
import com.example.giao_dien.databinding.DialogDatePickerBinding
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DatePickerDialogHelper {

    fun showCustomDatePickerDialog(
        context: Context,
        initialDateStr: String? = null,
        onDateSelected: (String) -> Unit
    ) {
        val binding = DialogDatePickerBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        if (!initialDateStr.isNullOrBlank()) {
            try {
                val parsedDate = sdf.parse(initialDateStr)
                if (parsedDate != null) {
                    val cal = Calendar.getInstance().apply { time = parsedDate }
                    binding.calendarView.setDate(cal)
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }

        // Bấm vào tiêu đề "Tháng - Năm" trên Header bộ lịch ➔ Bật Popup chọn nhanh Tháng & Năm
        val headerLabel = binding.calendarView.findViewById<View>(com.applandeo.materialcalendarview.R.id.currentDateLabel)
        headerLabel?.setOnClickListener {
            val currentCalendar = binding.calendarView.currentPageDate
            showMonthYearPickerDialog(context, currentCalendar) { selectedMonth, selectedYear ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                binding.calendarView.setDate(newCal)
            }
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSave.setOnClickListener {
            val selectedDates = binding.calendarView.selectedDates
            val selectedCalendar = if (selectedDates.isNotEmpty()) {
                selectedDates.first()
            } else {
                binding.calendarView.currentPageDate
            }

            val formattedDate = sdf.format(selectedCalendar.time)
            onDateSelected(formattedDate)
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Hiển thị Popup chọn nhanh Tháng (1-12) và Năm (1950-2050) cuộn phản hồi tức thì, siêu mượt
     */
    private fun showMonthYearPickerDialog(
        context: Context,
        currentCalendar: Calendar,
        onMonthYearSelected: (month: Int, year: Int) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_month_year_picker, null)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val pickerMonth = view.findViewById<NumberPicker>(R.id.pickerMonth)
        val pickerYear = view.findViewById<NumberPicker>(R.id.pickerYear)
        val btnCancel = view.findViewById<View>(R.id.btnCancelMonthYear)
        val btnConfirm = view.findViewById<View>(R.id.btnConfirmMonthYear)

        // Thiết lập NumberPicker cho Tháng (0 đến 11)
        val months = DateFormatSymbols(Locale.getDefault()).shortMonths
        pickerMonth?.minValue = 0
        pickerMonth?.maxValue = 11
        pickerMonth?.displayedValues = months
        pickerMonth?.value = currentCalendar.get(Calendar.MONTH)

        // Thiết lập NumberPicker cho Năm (1950 đến 2050)
        val currentYear = currentCalendar.get(Calendar.YEAR)
        pickerYear?.minValue = 1950
        pickerYear?.maxValue = 2050
        pickerYear?.value = currentYear

        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm?.setOnClickListener {
            val selectedMonth = pickerMonth?.value ?: currentCalendar.get(Calendar.MONTH)
            val selectedYear = pickerYear?.value ?: currentCalendar.get(Calendar.YEAR)
            onMonthYearSelected(selectedMonth, selectedYear)
            dialog.dismiss()
        }

        dialog.show()
    }
}
