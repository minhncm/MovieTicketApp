package com.example.ticketapp.data.network;

import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Res.BookingRes;
import com.example.ticketapp.domain.model.Res.CinemaRes;
import com.example.ticketapp.domain.model.Res.ReviewRequest;
import com.example.ticketapp.domain.model.Res.ReviewRes;
import com.example.ticketapp.domain.model.Res.TicketRes;
import com.example.ticketapp.domain.model.Showtimes;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.domain.model.Account;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @GET("getMovies")
        // endpoint Firebase Functions
    Call<List<Movie>> getMovies();

    @GET("getCinemas")
    Call<CinemaRes> getCinemas(@Query("city") String city);
    @GET("getCinemas")
    Call<CinemaRes> getAllCinemas(@Query("limit") int limit);

    @GET("getShowtimes")
    Call<List<Showtimes>> getShowTimes(@Query("date") String date, @Query("cinemaId") String cinemaID
    , @Query("movieId") String movieID);
    @POST("bookTicket")
    Call<BookingRes> bookTicket(@Body BookingData body);
    @GET("getBookingsByUserId")
    Call<TicketRes> fetchTickets(@Query("filter") String type, @Query("userId") String userId);

    @GET("getUserProfile")
    Call<Account> getUserProfile(@Query("uid") String uid);

    // Review APIs
    @POST("createReview")
    Call<ReviewRes> createReview(@Body ReviewRequest request);

    @PUT("updateReview")
    Call<ReviewRes> updateReview(@Body MovieReview review);

    @DELETE("deleteReview")
    Call<ReviewRes> deleteReview(@Query("reviewId") String reviewId, @Query("userId") String userId);

    @GET("getReviewsByMovie")
    Call<ReviewRes> getReviewsByMovie(@Query("movieId") String movieId);

    @GET("getReviewsByUser")
    Call<ReviewRes> getReviewsByUser(@Query("userId") String userId);

}

