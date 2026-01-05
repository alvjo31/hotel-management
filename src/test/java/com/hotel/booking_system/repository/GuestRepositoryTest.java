package com.hotel.booking_system.repository;


import com.hotel.booking_system.model.Guest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class GuestRepositoryTest {

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void testSaveGuest() {
        Guest guest = new Guest();
        guest.setFirstName("Alice");
        guest.setLastName("Smith");
        guest.setEmail("alice@example.com");
        guest.setPhone("987654321");

        Guest saved = guestRepository.save(guest);

        assertNotNull(saved.getId());
        assertEquals("Alice", saved.getFirstName());
    }

   /* @Test
    void testFindByEmail() {
        Guest guest = new Guest();
        guest.setFirstName("Bob");
        guest.setLastName("Brown");
        guest.setEmail("bob@example.com");
        guest.setPhone("111222333");
        guestRepository.save(guest);

        Optional<Guest> found = guestRepository.findByEmail("bob@example.com");
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
    }*/
}
