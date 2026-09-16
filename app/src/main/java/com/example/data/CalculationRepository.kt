package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val history: Flow<List<CalculationEntity>> = dao.getAllCalculations()

    suspend fun save(item: CalculationEntity) {
        dao.insertCalculation(item)
    }

    suspend fun delete(item: CalculationEntity) {
        dao.deleteCalculation(item)
    }

    suspend fun clear() {
        dao.clearAll()
    }
}
