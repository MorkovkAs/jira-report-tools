package ru.morkovka.report.entity

data class ReleaseNote(
    /**
     * Задача в нашей джире
     */
    var taskInKey: String,

    /**
     * Исходные коды
     */
    var sourceCode: MutableMap<String, MutableList<String>>,

    /**
     * Артефакты
     */
    var artifact: MutableMap<String, MutableList<String>>,

    /**
     * Новые функции и исправления
     */
    var features: MutableMap<String, TaskFeature>,

    /**
     * Настройки и изменения в структуре БД
     */
    var dbChanges: MutableMap<String, MutableList<String>>,

    /**
     * Информация для подключений и мониторинга
     */
    var monitoringChanges: MutableMap<String, MutableList<String>>,

    /**
     * Конфигурация
     */
    var configs: MutableMap<String, MutableList<String>>,

    /**
     * Порядок установки и изменения настроек
     */
    var deploy: MutableMap<String, MutableList<String>>,

    /**
     * План тестирования
     */
    var testCase: MutableMap<String, MutableList<String>>,

    /**
     * План отката
     */
    var rollback: MutableMap<String, MutableList<String>>
)