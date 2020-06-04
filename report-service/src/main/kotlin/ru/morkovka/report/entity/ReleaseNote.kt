package ru.morkovka.report.entity

import kotlin.collections.ArrayList

data class ReleaseNote (
    /**
     * Задача в нашей джире
     */
    var taskIn: String = String(),

    var sourceCode: String = String(),

    var artifact: String = String(),

    /**
     * Новые функции и исправления
     */
    var features: MutableList<String> = ArrayList(),

    /**
     * Настройки и изменения в структуре БД
     */
    var dbChanges: MutableList<String> = ArrayList(),

    var monitoringChanges: MutableList<String> = ArrayList(),

    /**
     * Конфигурация
     */
    var configs: MutableList<String> = ArrayList(),

    /**
     * Порядок установки и изменения настроек
     */
    var deploy: MutableList<String> = ArrayList(),

    /**
     * План тестирования
     */
    var testCase: MutableList<String> = ArrayList(),

    /**
     * План отката
     */
    var rollback: MutableList<String> = ArrayList()
)