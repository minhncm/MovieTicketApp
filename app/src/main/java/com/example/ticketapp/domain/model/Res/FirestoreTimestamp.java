package com.example.ticketapp.domain.model.Res;

import java.util.Date;

public class FirestoreTimestamp {
    private long _seconds;
    private int _nanoseconds;

    // Cần một constructor rỗng để thư viện parse JSON hoạt động
    public FirestoreTimestamp() {}

    // Getters and Setters
    public long get_seconds() {
        return _seconds;
    }

    public void set_seconds(long _seconds) {
        this._seconds = _seconds;
    }

    public int get_nanoseconds() {
        return _nanoseconds;
    }

    public void set_nanoseconds(int _nanoseconds) {
        this._nanoseconds = _nanoseconds;
    }

    // Hàm tiện ích để chuyển đổi sang đối tượng Date của Java
    public Date toDate() {
        // Chuyển đổi seconds sang milliseconds
        return new Date(_seconds * 1000);
    }
}