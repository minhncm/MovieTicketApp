package com.example.ticketapp.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.SeatAdapter;
import com.example.ticketapp.databinding.FragmentSelectSeatBinding;
import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Room;
import com.example.ticketapp.domain.model.Seat;
import com.example.ticketapp.domain.model.Showtimes; // Đảm bảo bạn đã import Showtimes
import com.example.ticketapp.utils.Resource;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.example.ticketapp.viewmodel.CinemaViewModel;
import com.example.ticketapp.viewmodel.MovieViewModel;
import com.example.ticketapp.viewmodel.ProfileViewModel;
import com.example.ticketapp.viewmodel.SavedPlanViewModel;
import com.example.ticketapp.domain.model.SavedPlanEntity;
import com.example.ticketapp.domain.model.Movie;

import android.widget.Toast;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SelectSeatFragment extends Fragment {
    private FragmentSelectSeatBinding binding;
    private final Calendar myCalendar = Calendar.getInstance();
    private CinemaViewModel cinemaViewModel;
    private MovieViewModel movieViewModel;
    private SeatAdapter seatAdapter;
    private RecyclerView recyclerViewSeats;
    private ProfileViewModel profileViewModel;
    private BookingViewModel bookingViewModel;
    private SavedPlanViewModel savedPlanViewModel;
    private Movie currentMovie;
    private Showtimes selectedShowtime;
    private int selectedCinemaPosition = AdapterView.INVALID_POSITION;
    private List<Cinema> currentCinemaList = new ArrayList<>();
    private List<Showtimes> currentShowtimeList = new ArrayList<>();
    private String date;
    private String selectedCity;
    private BookingData bookingData = new BookingData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectSeatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(SelectSeatFragment.this);

        // Khởi tạo ViewModel trước
        cinemaViewModel = new ViewModelProvider(requireActivity()).get(CinemaViewModel.class);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        savedPlanViewModel = new ViewModelProvider(this).get(SavedPlanViewModel.class);
        seatAdapter = new SeatAdapter((seat, position, isSelected) -> {
        });
        recyclerViewSeats = binding.recyclerViewSeats;
        recyclerViewSeats.setLayoutManager(new GridLayoutManager(requireContext(), 10));
        recyclerViewSeats.setAdapter(seatAdapter);
        setUpBtn(navController);
        setUpViewModelObservers();
        setUpDatePicker();
        setupCitySpinner();
        setUpCinemaChoice();
        setUpShowtimeChoice();
    }

    private void setUpBtn(NavController navController) {
        binding.buttonCheckout.setOnClickListener(view1 -> {
            List<String> selectedSeats = seatAdapter.getSelectedSeatIds();
            
            if (selectedShowtime == null) {
                Toast.makeText(requireContext(), R.string.txt_select_showtime_first, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedSeats.isEmpty()) {
                Toast.makeText(requireContext(), R.string.txt_select_seats_first, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (bookingData.getUserId() == null) {
                Toast.makeText(requireContext(), R.string.txt_login_required, Toast.LENGTH_SHORT).show();
                return;
            }
            
            bookingData.setSelectedSeats(selectedSeats);
            bookingData.setShowTimeId(selectedShowtime.getUid());
            bookingViewModel.setBookingData(bookingData);
            seatAdapter.clearSelection();
            // Navigate đến màn hình chọn phương thức thanh toán
            navController.navigate(R.id.action_selectSeatFragment_to_paymentMethodSelection);
        });
        
        binding.buttonSavePlan.setOnClickListener(view1 -> {
            savePlanForLater();
        });
    }

    private void setUpViewModelObservers() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), account -> {
            if (account.getUid() != null) {
                bookingData.setUserId(account.getUid());
            }

        });


        // Lấy phim đã chọn (từ màn hình trước)
        movieViewModel.selectedMovie.observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                currentMovie = movie;
                cinemaViewModel.setMovieSelected(movie.getId());
            }
        });

        // Tự động cập nhật rạp khi 'setCity' được gọi
        cinemaViewModel.getCinemasByCity().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                updateCinemaSpinner(new ArrayList<>());

                return;
            }
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                if (resource.getData().isEmpty()) {
                    updateCinemaSpinner(new ArrayList<>());
                    updateShowtimeSpinner(new ArrayList<>());
                }
                updateCinemaSpinner(resource.getData());
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                updateCinemaSpinner(new ArrayList<>());
            }
        });

        // Tự động cập nhật suất chiếu khi 'setMovieSelected', 'setCinemaID', hoặc 'setDate' được gọi
        cinemaViewModel.getShowTimes().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                updateShowtimeSpinner(new ArrayList<>());
                return;
            }
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {

                updateShowtimeSpinner(resource.getData());

            } else if (resource.getStatus() == Resource.Status.ERROR) {
                updateShowtimeSpinner(new ArrayList<>());
            }
        });
    }

    private void setupCitySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cities_array,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                seatAdapter.setSeats(new ArrayList<>());
                selectedCity = adapterView.getItemAtPosition(position).toString();
                if (!selectedCity.isEmpty()) {
                    // Kích hoạt 'getCinemasByCity'
                    cinemaViewModel.setCity(selectedCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        binding.spinnerCity.setAdapter(adapter);
    }

    private void setUpDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        binding.textViewDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            // 🔥 SỬA ĐỔI: Lấy thời điểm hiện tại chính xác (System Time / NOW)
            // Không đặt lại giờ, phút, giây về 00:00:00 nữa.
            Calendar today = Calendar.getInstance();

            // ÁP DỤNG GIỚI HẠN TỐI THIỂU
            // Lấy milliseconds chính xác của thời điểm hiện tại (NOW)
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());

            datePickerDialog.show();
        });

        updateLabel();
    }

    private void updateLabel() {
        seatAdapter.setSeats(new ArrayList<>());
        // Sửa 2: Lỗi định dạng 'DD'
        // Dùng "dd" (ngày trong tháng) thay vì "DD" (ngày trong năm)
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = sdf.format(myCalendar.getTime());

        binding.textViewDate.setText(formattedDate);
        date = formattedDate;

        // Sửa 3: Kích hoạt ViewModel khi ngày thay đổi
        // Phải kiểm tra null vì 'updateLabel' được gọi trước 'setUpViewModel'
        if (cinemaViewModel != null) {
            cinemaViewModel.setDate(date);
        }
    }

    private void updateCinemaSpinner(List<Cinema> cinemaList) {
        // Sửa 4a: Cập nhật List<Cinema>
        currentCinemaList.clear();
        currentCinemaList.addAll(cinemaList);

        // Chuyển đổi thành tên
        List<String> cinemaNames = new ArrayList<>();
        for (Cinema cinema : cinemaList) {
            cinemaNames.add(cinema.getName());
        }

        ArrayAdapter<String> cinemaAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                cinemaNames
        );
        cinemaAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        binding.spinnerCinema.setAdapter(cinemaAdapter);
    }

    private void setUpCinemaChoice() {

        binding.spinnerCinema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Sửa 4b: Lấy ID từ List (an toàn và dễ hơn)
                if (position < currentCinemaList.size()) {
                    String selectedCinemaId = currentCinemaList.get(position).getUid();
                    selectedCinemaPosition = position;
                    // Lưu vị trí rạp đã chọn

                    if (selectedCinemaId != null) {
                        // Kích hoạt 'getShowTimes'
                        cinemaViewModel.setCinemaID(selectedCinemaId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setUpShowtimeChoice() {

        binding.spinnerShowtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position < currentShowtimeList.size() &&
                        selectedCinemaPosition != AdapterView.INVALID_POSITION && // Kiểm tra vị trí hợp lệ
                        selectedCinemaPosition < currentCinemaList.size()) {

                    // 1. Lấy Suất chiếu và Rạp phim
                    selectedShowtime = currentShowtimeList.get(position);
                    Cinema selectedCinema = currentCinemaList.get(selectedCinemaPosition);

                    // 2. Tìm thông tin phòng chiếu (Room)
                    Room selectedRoom = null;
                    for (Room room : selectedCinema.getRooms()) { // Giả sử Cinema có getRooms()
                        if (room.getRoomName().equals(selectedShowtime.getRoomName())) {
                            selectedRoom = room;
                            break;
                        }
                    }

                    // 3. Nếu tìm thấy phòng, lấy số cột
                    int spanCount = 10; // Mặc định 10 cột nếu không tìm thấy
                    if (selectedRoom != null) {
                        // Giả sử Room có getSeatsPerRow()
                        spanCount = selectedRoom.getSeatsPerRow();
                    }

                    // 4. (QUAN TRỌNG) Tạo và gán GridLayoutManager
                    GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
                    recyclerViewSeats.setLayoutManager(layoutManager);

                    // 5. Gán dữ liệu ghế cho Adapter
                    List<Seat> seats = selectedShowtime.getSeats();
                    Log.d("SelectSeatFragment", "Seats count: " + (seats != null ? seats.size() : "null"));
                    if (seats != null && !seats.isEmpty()) {
                        seatAdapter.setSeats(seats);
                    } else {
                        seatAdapter.setSeats(new ArrayList<>());
                    }
                } else {
                    // Xóa ghế nếu không có suất chiếu hoặc rạp phim
                    seatAdapter.setSeats(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateShowtimeSpinner(List<Showtimes> showtimeList) {
        // Sửa 5: Định dạng lại giờ và tạo Adapter bên ngoài vòng lặp
        currentShowtimeList.clear();
        currentShowtimeList.addAll(showtimeList);

        List<String> formattedShowtimes = new ArrayList<>();
        // Dùng SimpleDateFormat để format giờ: "19:30"
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);

        for (Showtimes showtime : showtimeList) {
            // Chuyển Date object thành chuỗi "HH:mm"
            formattedShowtimes.add(timeFormatter.format(showtime.getStartTime()));
        }

        // Tạo Adapter BÊN NGOÀI vòng lặp
        ArrayAdapter<String> showtimeAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                formattedShowtimes // Dùng List<String> đã được định dạng
        );

        showtimeAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        binding.spinnerShowtime.setAdapter(showtimeAdapter);
    }

    private void savePlanForLater() {
        // Validate dữ liệu
        if (currentMovie == null) {
            Toast.makeText(requireContext(), R.string.txt_select_movie_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCinemaPosition == AdapterView.INVALID_POSITION || currentCinemaList.isEmpty()) {
            Toast.makeText(requireContext(), R.string.txt_select_cinema_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedShowtime == null) {
            Toast.makeText(requireContext(), R.string.txt_select_showtime_first, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedSeats = seatAdapter.getSelectedSeatIds();
        if (selectedSeats.isEmpty()) {
            Toast.makeText(requireContext(), R.string.txt_select_seats_first, Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo SavedPlanEntity
        SavedPlanEntity plan = new SavedPlanEntity();
        plan.setMovieId(currentMovie.getId());
        plan.setMovieTitle(currentMovie.getTitle());
        plan.setMoviePoster(currentMovie.getPosterUrl());
        plan.setGenre(currentMovie.getGenres().toString());
        plan.setRating(currentMovie.getRating());
        plan.setDuration(currentMovie.getDuration());

        // Cinema info
        Cinema selectedCinema = currentCinemaList.get(selectedCinemaPosition);
        plan.setCinemaId(selectedCinema.getUid());
        plan.setCinemaName(selectedCinema.getName());

        // Showtime ID - quan trọng để checkout sau
        plan.setShowtimeId(selectedShowtime.getUid());

        // Date & Time
        plan.setDate(date);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        plan.setTime(timeFormatter.format(selectedShowtime.getStartTime()));
        
        // Seats
        plan.setSelectedSeats(String.join(",", selectedSeats));
        plan.setPersonCount(selectedSeats.size());
        
        // Lưu vào database
        savedPlanViewModel.insert(plan);
        Toast.makeText(requireContext(), R.string.txt_plan_saved, Toast.LENGTH_SHORT).show();
        
        // Clear selected seats
        seatAdapter.clearSelection();
        
        // Navigate to saved plans
        NavController navController = NavHostFragment.findNavController(SelectSeatFragment.this);
        navController.navigate(R.id.action_selectSeatFragment_to_nav_bookmark);
    }
}