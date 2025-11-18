package com.example.ticketapp.domain.model;

import java.lang.reflect.Array;
import java.util.List;

public class Room {
    private String roomName;
    private List<RowType> rowTypes;
    private int seatsPerRow;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    public List<RowType> getRowTypes() {
        return rowTypes;
    }

    public void setRowTypes(List<RowType> rowTypes) {
        this.rowTypes = rowTypes;
    }

    private int totalRows;
public static class RowType{
    private List<String> rows;
    private String type;

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
}

