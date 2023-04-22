package org.example.Models;

enum Status {
    WAITING,
    IN_PROGRESS,
    READY,
}

public class CrackHashStatus {
    public Status status;
    public String data;

    public CrackHashStatus() {
        status = Status.WAITING;
        data = null;
    }
}
