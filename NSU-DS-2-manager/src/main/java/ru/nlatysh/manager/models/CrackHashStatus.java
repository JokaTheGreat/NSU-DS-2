package ru.nlatysh.manager.models;

import java.util.List;

public class CrackHashStatus {
    public enum Status {
        WAITING,
        IN_PROGRESS,
        READY,
        ERROR,
    }

    public Status status;
    public List<String> data;

    public CrackHashStatus() {
        status = Status.WAITING;
        data = null;
    }
}
