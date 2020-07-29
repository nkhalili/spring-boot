package com.navid.learningspring.business.service;

import com.navid.learningspring.business.domain.GuestViewModel;
import com.navid.learningspring.business.domain.RoomReservation;
import com.navid.learningspring.data.entity.Guest;
import com.navid.learningspring.data.entity.Reservation;
import com.navid.learningspring.data.entity.Room;
import com.navid.learningspring.data.repository.GuestRepository;
import com.navid.learningspring.data.repository.ReservationRepository;
import com.navid.learningspring.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date){
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getRoomId());
            roomReservation.setRoomName(room.getRoomName());
            roomReservation.setRoomNumber(room.getRoomNumber());

            roomReservationMap.put(room.getRoomId(), roomReservation);
        });

        Iterable<Reservation> reservations = this.reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime()));
        reservations.forEach(reservation -> {
            RoomReservation roomReservation = roomReservationMap.get(reservation.getRoomId());
            roomReservation.setDate(date);
            Guest guest = this.guestRepository.findById(reservation.getGuestId()).get();
            roomReservation.setFirstName(guest.getFirstName());
            roomReservation.setLastName(guest.getLastName());
            roomReservation.setGuestId(guest.getGuestId());
        });

        List<RoomReservation> roomReservations = new ArrayList<>();
        for (Long id : roomReservationMap.keySet()){
            roomReservations.add(roomReservationMap.get(id));
        }

        roomReservations.sort(new Comparator<RoomReservation>() {
            @Override
            public int compare(RoomReservation o1, RoomReservation o2) {
                if(o1.getRoomName() == o2.getRoomName()) {
                    return o1.getRoomNumber().compareTo(o2.getRoomNumber());
                }
                return o1.getRoomName().compareTo(o2.getRoomName());
            }
        });

        return roomReservations;
    }

    public List<GuestViewModel> getGuests(){
        Iterable<Guest> guestList = this.guestRepository.findAll();
        List<GuestViewModel> guests = new ArrayList<>();

        guestList.forEach(guest -> {
            GuestViewModel model = new GuestViewModel();

            model.setLastName(guest.getLastName());
            model.setFirstName(guest.getFirstName());
            model.setEmailAddress(guest.getEmailAddress());
            model.setPhoneNumber(guest.getPhoneNumber());

            guests.add(model);
        });

        guests.sort(new Comparator<GuestViewModel>() {
            @Override
            public int compare(GuestViewModel o1, GuestViewModel o2) {
                if(o1.getLastName() == o2.getLastName()){
                    return o1.getFirstName().compareTo(o2.getFirstName());
                }
                return o1.getLastName().compareTo(o2.getLastName());
            }
        });

        return guests;
    }
}
