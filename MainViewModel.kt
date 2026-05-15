package com.nammaraste.reporter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nammaraste.reporter.data.AppDatabase
import com.nammaraste.reporter.data.ReportEntity
import com.nammaraste.reporter.data.ReportRepository
import com.nammaraste.reporter.utils.TicketIdGenerator
import kotlinx.coroutines.launch

/**
 * ViewModel for MainActivity — manages the report list and new-report submissions.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReportRepository
    val allReports: LiveData<List<ReportEntity>>

    init {
        val dao = AppDatabase.getInstance(application).reportDao()
        repository = ReportRepository(dao)
        allReports = repository.allReports
    }

    /**
     * Saves a new report and returns its generated Ticket ID via [onTicketGenerated].
     */
    fun submitReport(
        issueType: String,
        latitude: Double,
        longitude: Double,
        photoPath: String,
        submittedBy: String,
        onTicketGenerated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val prefix = TicketIdGenerator.todayPrefix()
            val todayCount = repository.countReportsForDate(prefix)
            val ticketId = TicketIdGenerator.generate(todayCount)

            val report = ReportEntity(
                ticketId = ticketId,
                issueType = issueType,
                latitude = latitude,
                longitude = longitude,
                timestamp = System.currentTimeMillis(),
                photoPath = photoPath,
                submittedBy = submittedBy
            )
            repository.insertReport(report)
            onTicketGenerated(ticketId)
        }
    }
}
