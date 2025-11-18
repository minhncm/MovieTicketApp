package com.example.ticketapp.domain.model.Res;

import com.example.ticketapp.domain.model.Cinema;

import java.util.List;

public class CinemaRes {
    List<Cinema> cinemas;

    public List<Cinema> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<Cinema> cinemas) {
        this.cinemas = cinemas;
    }

    public CinemaRes(List<Cinema> cinemas) {
        this.cinemas = cinemas;
    }
}
