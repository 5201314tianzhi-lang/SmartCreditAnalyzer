package com.smart.credit.analyzer.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreditReportViewModel(
    private val repository: com.smart.credit.analyzer.repository.CreditReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreditReportUiState>(CreditReportUiState())
    val uiState: StateFlow<CreditReportUiState> get() = _uiState

    val isLoading: StateFlow<Boolean> = repository.isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
            } catch (e: Exception) {
                _errorMessage.value = "加载失败: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun analyzeReport(reportId: String) {
        viewModelScope.launch {
            val report = repository.loadReport(reportId) ?: return@launch
            val analyzed = repository.analyzeReport(report)
            val uiModel = analyzed.toUiModel()
            _uiState.value = _uiState.value.copy(selectedReport = uiModel)
        }
    }

    suspend fun saveReport(report: com.smart.credit.analyzer.data.model.CreditReport): String {
        return repository.saveReport(report)
    }

    fun updateReport(report: com.smart.credit.analyzer.data.model.CreditReport) {
        viewModelScope.launch {
            try {
                repository.updateReport(report)
            } catch (e: Exception) {
                _errorMessage.value = "更新失败: ${e.message}"
            }
        }
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            try {
                repository.deleteReport(reportId)
                if (_uiState.value.selectedReport?.reportId == reportId) {
                    _uiState.value = _uiState.value.copy(selectedReport = null)
                }
            } catch (e: Exception) {
                _errorMessage.value = "删除失败: ${e.message}"
            }
        }
    }

    fun filterReportsByScore(minScore: Int, maxScore: Int) {
        val filtered = repository.getReportsByScore(minScore, maxScore)
        _uiState.value = _uiState.value.copy(filteredReports = filtered)
    }

    fun getStats(): Map<String, Any> = repository.getStats()

    fun clearError() {
        viewModelScope.launch {
            _errorMessage.value = null
        }
    }
}

data class CreditReportUiState(
    val selectedReport: com.smart.credit.analyzer.presentation.model.CreditReportUiModel? = null,
    val filteredReports: List<com.smart.credit.analyzer.presentation.model.CreditReportUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val lastAnalyzedDate: java.time.LocalDateTime? = null
)