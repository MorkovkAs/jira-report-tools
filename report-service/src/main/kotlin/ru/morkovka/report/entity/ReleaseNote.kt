package ru.morkovka.report.entity

import kotlin.collections.ArrayList

class ReleaseNote {
    /**
     * Задача в нашей джире
     */
    var taskIn: String? = String()

    var sourceCode: String = String()

    var artifact: String = String()

    /**
     * Новые функции и исправления
     */
    var features: MutableList<String>? = ArrayList()

    /**
     * Настройки и изменения в структуре БД
     */
    var dbChanges: MutableList<String>? = ArrayList()

    var monitoringChanges: MutableList<String>? = ArrayList()

    /**
     * Конфигурация
     */
    var configs: MutableList<String>? = ArrayList()

    /**
     * Порядок установки и изменения настроек
     */
    var deploy: MutableList<String>? = ArrayList()

    /**
     * План тестирования
     */
    var testCase: MutableList<String>? = ArrayList()

    /**
     * План отката
     */
    var rollback: MutableList<String>? = ArrayList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReleaseNote

        if (taskIn != other.taskIn) return false
        if (sourceCode != other.sourceCode) return false
        if (artifact != other.artifact) return false
        if (features != other.features) return false
        if (dbChanges != other.dbChanges) return false
        if (monitoringChanges != other.monitoringChanges) return false
        if (configs != other.configs) return false
        if (deploy != other.deploy) return false
        if (testCase != other.testCase) return false
        if (rollback != other.rollback) return false

        return true
    }

    override fun hashCode(): Int {
        var result = taskIn?.hashCode() ?: 0
        result = 31 * result + sourceCode.hashCode()
        result = 31 * result + artifact.hashCode()
        result = 31 * result + (features?.hashCode() ?: 0)
        result = 31 * result + (dbChanges?.hashCode() ?: 0)
        result = 31 * result + (monitoringChanges?.hashCode() ?: 0)
        result = 31 * result + (configs?.hashCode() ?: 0)
        result = 31 * result + (deploy?.hashCode() ?: 0)
        result = 31 * result + (testCase?.hashCode() ?: 0)
        result = 31 * result + (rollback?.hashCode() ?: 0)
        return result
    }

}