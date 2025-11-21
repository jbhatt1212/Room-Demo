package com.example.cashmachine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashmachine.repository.CashRepository
import kotlinx.coroutines.launch


class CashViewModel(private val repo: CashRepository) : ViewModel() {

    val summary = repo.summary
    val transactions = repo.transactions

    init {
        viewModelScope.launch {
            repo.createSummaryIfNotExists()
        }
    }

    fun credit(a500: Long, a200: Long, a100: Long, a50: Long, a20: Long, a10: Long) {
        viewModelScope.launch {
            repo.credit(a500, a200, a100, a50, a20, a10)
        }
    }

    fun debit(a500: Long, a200: Long, a100: Long, a50: Long, a20: Long, a10: Long) {
        viewModelScope.launch {
            repo.debit(a500, a200, a100, a50, a20, a10)
        }
    }
    fun clearHistoryAndSummary() {
        viewModelScope.launch {
            repo.clearAllData()
        }
    }


}
