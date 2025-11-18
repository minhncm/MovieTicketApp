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
    private Showtimes selectedShowtime;
    private int selectedCinemaPosition = AdapterView.INVALID_POSITION;
    private List<Cinema> currentCinemaList = new ArrayList<>();
    private List<Showtimes> currentShowtimeList = new ArrayList<>();
    private String date;
    private String selectedCity;
    private List<String> selectdSeats = new ArrayList<>();
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
        // Khởi tạo ViewModel trước
        cinemaViewModel = new ViewModelProvider(requireActivity()).get(CinemaViewModel.class);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        seatAdapter = new SeatAdapter((seat, position) -> {
            selectdSeats.add(seat.getSeatId());
        });
        recyclerViewSeats = binding.recyclerViewSeats;
        recyclerViewSeats.setAdapter(seatAdapter);
        binding.buttonCheckout.setOnClickListener(view1 -> {
            bookingData.setShowTimeId(selectedShowtime.getUid());
            if (!selectdSeats.isEmpty() && bookingData.getUserId() != null && selectedShowtime != null) {
                bookingData.setSelectedSeats(selectdSeats);
                bookingData.setShowTimeId(selectedShowtime.getUid());
                bookingViewModel.setBookingData(bookingData);
                selectdSeats.clear();
                NavController navController = NavHostFragment.findNavController(SelectSeatFragment.this);
                navController.navigate(R.id.action_selectSeatFragment_to_paymentMethod);
            }
        });
        // Setup các Observers
        setUpViewModelObservers();
        // Setup các Views
        setUpDatePicker();
        setupCitySpinner();
        setUpCinemaChoice();
        setUpShowtimeChoice();
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
                    seatAdapter.setSeats(seats);
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
}