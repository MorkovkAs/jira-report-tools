package ru.morkovka.report.entity

import java.util.*
import kotlin.collections.ArrayList

class ReleaseNote {
    /**
     * Дистрибутивы
     */
    var distributions: String? = String()

    /**
     * Новые функции и исправления
     */
    var changes: MutableList<String>? = ArrayList()

    /**
     * Настройки и изменения в структуре БД
     */
    var dbChanges: MutableList<String>? = ArrayList()

    /**
     * Конфигурация
     */
    var configs: MutableList<String>? = ArrayList()

    /**
     * Порядок установки и изменения настроек
     */
    var installation: MutableList<String>? = ArrayList()

    /**
     * План тестирования
     */
    var testing: MutableList<String>? = ArrayList()

    /**
     * План отката
     */
    var rollback: MutableList<String>? = ArrayList()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReleaseNote

        if (distributions != other.distributions) return false
        if (changes != other.changes) return false
        if (dbChanges != other.dbChanges) return false
        if (configs != other.configs) return false
        if (installation != other.installation) return false
        if (testing != other.testing) return false
        if (rollback != other.rollback) return false

        return true
    }

    override fun hashCode(): Int {
        var result = distributions?.hashCode() ?: 0
        result = 31 * result + (changes?.hashCode() ?: 0)
        result = 31 * result + (dbChanges?.hashCode() ?: 0)
        result = 31 * result + (configs?.hashCode() ?: 0)
        result = 31 * result + (installation?.hashCode() ?: 0)
        result = 31 * result + (testing?.hashCode() ?: 0)
        result = 31 * result + (rollback?.hashCode() ?: 0)
        return result
    }


}