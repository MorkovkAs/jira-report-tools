package ru.morkovka.report.entity;

import java.util.ArrayList;

public class ReleaseNote {
    private String distributions;
    private ArrayList<String> changes;
    private String dbChanges;
    private String configs;
    private String installation;
    private String testing;
    private String rollback;

    public String getDistributions() {
        return distributions;
    }

    public void setDistributions(String distributions) {
        this.distributions = distributions;
    }

    public ArrayList<String> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<String> changes) {
        this.changes = changes;
    }

    public String getDbChanges() {
        return dbChanges;
    }

    public void setDbChanges(String dbChanges) {
        this.dbChanges = dbChanges;
    }

    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getTesting() {
        return testing;
    }

    public void setTesting(String testing) {
        this.testing = testing;
    }

    public String getRollback() {
        return rollback;
    }

    public void setRollback(String rollback) {
        this.rollback = rollback;
    }
}
