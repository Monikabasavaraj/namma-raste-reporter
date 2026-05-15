package com.nammaraste.reporter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nammaraste.reporter.data.AppDatabase
import com.nammaraste.reporter.data.ReportEntity
import com.nammaraste.reporter.data.ReportRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for TicketTrackerActivity.
 */
class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReportRepository

    val searchResult = MutableLiveData<ReportEntity?>()
    val isLoading   = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    init {
        val dao = AppDatabase.getInstance(application).reportDao()
        repository = ReportRepository(dao)
    }

    fun searchByTicketId(ticketId: String) {
        if (ticketId.isBlank()) {
            errorMessage.value = "Please enter a Ticket ID."
            return
        }
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            val result = repository.getReportByTicketId(ticketId.trim().uppercase())
            searchResult.postValue(result)
            if (result == null) {
                errorMessage.postValue("No report found for ticket: $ticketId")
            }
            isLoading.postValue(false)
        }
    }
}
