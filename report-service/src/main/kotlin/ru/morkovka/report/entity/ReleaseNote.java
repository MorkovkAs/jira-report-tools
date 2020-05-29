package ru.morkovka.report.entity;

import java.util.ArrayList;

public class ReleaseNote {
    private String distributions;
    private ArrayList<String> changes;
    private ArrayList<String> dbChanges;
    private ArrayList<String> configs;
    private ArrayList<String> installation;
    private ArrayList<String> testing;
    private ArrayList<String> rollback;

    /**
     * Дистрибутивы
     * **/
    public String getDistributions() {
        return distributions;
    }

    public void setDistributions(String distributions) {
        this.distributions = distributions;
    }

    /**
     * Новые функции и исправления
     * **/
    public ArrayList<String> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<String> changes) {
        this.changes = changes;
    }

    /**
     * Настройки и изменения в структуре БД
     * **/
    public ArrayList<String> getDbChanges() {
        return dbChanges;
    }

    public void setDbChanges(ArrayList<String> dbChanges) {
        this.dbChanges = dbChanges;
    }

    /**
     * Конфигурация
     * **/
    public ArrayList<String> getConfigs() {
        return configs;
    }

    public void setConfigs(ArrayList<String> configs) {
        this.configs = configs;
    }

    /**
     * Порядок установки и изменения настроек
     * **/
    public ArrayList<String> getInstallation() {
        return installation;
    }

    public void setInstallation(ArrayList<String> installation) {
        this.installation = installation;
    }

    /**
     * План тестирования
     * **/
    public ArrayList<String> getTesting() {
        return testing;
    }

    public void setTesting(ArrayList<String> testing) {
        this.testing = testing;
    }

    /**
     * План отката
     * **/
    public ArrayList<String> getRollback() {
        return rollback;
    }

    public void setRollback(ArrayList<String> rollback) {
        this.rollback = rollback;
    }
}
