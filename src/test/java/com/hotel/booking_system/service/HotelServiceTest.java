package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.HotelDtoMapper;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.repository.HotelRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HotelDtoMapper hotelDtoMapper;
    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private HotelDto hotelDto;

    @BeforeEach
    public void setUp() {
        //KY ESHTE NJE OBJEKT QE DO TA PERDORIM GJATE GJITH KOHES
        hotel = new Hotel();
        hotel.setId(1);
        hotel.setHotelName("Hotel Name");
        hotel.setHotelAddress("Hotel Address");
        hotel.setHotelCity("Hotel City");
        hotel.setHotelDescription("Hotel Description");
        hotel.setHotelEmail("Hotel Email");
        hotel.setHotelPhone(06777777777);
        hotel.setCreatedDate(LocalDate.of(2024, 1, 1));
        hotel.setUpdatedDate(LocalDate.of(2024, 1, 1));


        hotelDto = new HotelDto(1
                , "Hotel Name"
                , "Hotel Address"
                , "Hotel City"
                , "Hotel Description"
                , 123
                , "Hotel Email"
                , LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );

    }

    @Test
    void testGetHotelById_WhenHotelExists_ReturnsHotelDto() {
        //given  -- pergaqtitja
        int hotelId = 1;
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(hotelDtoMapper.apply(hotel)).thenReturn(hotelDto);

        //when -- ekzekutimi
        HotelDto result = hotelService.getHotelById(hotel.getId());


        //then -- verifikimi

        assertNotNull(result);
        assertEquals("Hotel Name", result.getHotelName());
        assertEquals("Hotel Address", result.getHotelAddress());
        assertEquals("Hotel City", result.getHotelCity());
        assertEquals("Hotel Description", result.getHotelDescription());
        assertEquals(123, result.getHotelPhone());
        assertEquals("Hotel Email", result.getHotelEmail());
        assertEquals(1, result.getHotelId());

        verify(hotelRepository, times(1)).findById(hotelId);
        verify(hotelDtoMapper, times(1)).apply(hotel);
    }


    @Test
    void testGetHotelById_WhenHotelDoesNotExist_ThrowsException() {

        //given
        int hotelId = 999;
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        //when & then

        ResourceNotFindException exception = assertThrows(ResourceNotFindException.class,
                () -> hotelService.getHotelById(hotelId)

        );
        assertEquals("Hotel not found", exception.getMessage());
        verify(hotelRepository, times(1)).findById(hotelId);
        verify(hotelDtoMapper, never()).apply(any());
    }

    @Test
    void testGetHotelByName_WhenHotelExists_ReturnsHotelDto() {
        String hotelName = "Hotel Name";
        when(hotelRepository.getByHotelName(hotelName)).thenReturn(Optional.of(hotel));
        when(hotelDtoMapper.apply(hotel))
                .thenReturn(hotelDto);
        //When
        HotelDto result = hotelService.getHotelByName(hotelName);

        // THEN
        assertNotNull(result);
        assertEquals("Hotel Name", result.getHotelName());
        verify(hotelRepository, times(1)).getByHotelName(hotelName);
        verify(hotelDtoMapper, times(1)).apply(hotel);
    }


    @Test
    void testAddHotel_WithEmptyName_ThrowsBadRequestException() {
        // GIVEN
        HotelDto invalidDto = new HotelDto(
                null,
                "",  // Emër bosh
                "Tirane",
                "Rruga X",
                "+355691111111",
                123,
                "Pershkrim",
                null,
                null
        );
        // WHEN & THEN
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> hotelService.addHotel(invalidDto)
        );

        assertEquals("Emri i hotelit eshte i detyrueshem", exception.getMessage());
        verify(hotelRepository, never()).save(any());
    }

    @Test
    void testAddHotel_WithNullName_ThrowsBadRequestException() {
        // GIVEN
        HotelDto invalidDto = new HotelDto(
                null,
                null,
                "Tirane",
                "Rruga X",
                "+355691111111",
                123,
                "Pershkrim",
                null,
                null
        );

        // WHEN & THEN
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> hotelService.addHotel(invalidDto)
        );

        assertEquals("Emri i hotelit eshte i detyrueshem", exception.getMessage());
    }

    @Test
    void testAddHotel_WithEmptyCity_ThrowsBadRequestException() {
        // GIVEN
        HotelDto invalidDto = new HotelDto(
                null,
                "Hotel Name",
                "",  // Qytet bosh
                "Rruga X",
                "+355691111111",
                123,
                "Pershkrim",
                null,
                null
        );

        // WHEN & THEN
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> hotelService.addHotel(invalidDto)
        );

        assertEquals("Qyteti i hotelit eshte i detyrueshem", exception.getMessage());
    }

    @Test
    void testAddHotel_WithDuplicateName_ThrowsDuplicateResourceException() {
        // GIVEN
        HotelDto duplicateDto = new HotelDto(
                null,
                "Grand Hotel",
                "Tirane",
                "Rruga X",
                "+355691111111",
                123,
                "Pershkrim",
                null,
                null
        );

        when(hotelRepository.getByHotelName("Grand Hotel"))
                .thenReturn(Optional.of(hotel));

        // WHEN & THEN
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> hotelService.addHotel(duplicateDto)
        );

        assertTrue(exception.getMessage().contains("Grand Hotel"));
        assertTrue(exception.getMessage().contains("ekziston"));
        verify(hotelRepository, never()).save(any());
    }

    // ========== TESTET PËR updateHotel ==========

    @Test
    void testUpdateHotel_WithValidData_ReturnsUpdatedHotelDto() {
        // GIVEN
        int hotelId = 1;
        HotelDto updatedDto = new HotelDto(
                1,
                "Grand Hotel Updated",
                "Tirane",
                "Rruga e Re 456",
                "+355691234567",
                123,
                "Hotel i perditesuar",
                LocalDate.of(2024, 1, 1),
                LocalDate.now()
        );

        when(hotelRepository.findById(hotelId))
                .thenReturn(Optional.of(hotel));

        when(hotelDtoMapper.apply(hotel))
                .thenReturn(updatedDto);

        // WHEN
        HotelDto result = hotelService.updateHotel(hotelId, updatedDto);

        // THEN
        assertNotNull(result);
        assertEquals("Grand Hotel Updated", hotel.getHotelName());
        assertEquals("Rruga e Re 456", hotel.getHotelAddress());

        verify(hotelRepository, times(1)).findById(hotelId);
        verify(hotelDtoMapper, times(1)).apply(hotel);
    }

    @Test
    void testUpdateHotel_WhenHotelDoesNotExist_ThrowsException() {
        // GIVEN
        int hotelId = 999;
        HotelDto updatedDto = new HotelDto(
                999,
                "Hotel Name",
                "Tirane",
                "Address",
                "+355691234567",
                123,
                "Description",
                LocalDate.now(),
                LocalDate.now()
        );

        when(hotelRepository.findById(hotelId))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        ResourceNotFindException exception = assertThrows(
                ResourceNotFindException.class,
                () -> hotelService.updateHotel(hotelId, updatedDto)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(hotelId)));
        verify(hotelRepository, times(1)).findById(hotelId);
    }

    @Test
    void testUpdateHotel_WithEmptyName_ThrowsBadRequestException() {
        // GIVEN
        int hotelId = 1;
        HotelDto invalidDto = new HotelDto(
                1,
                "",  // Emër bosh
                "Tirane",
                "Address",
                "+355691234567",
                123,
                "Description",
                LocalDate.now(),
                LocalDate.now()
        );

        // WHEN & THEN
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> hotelService.updateHotel(hotelId, invalidDto)
        );

        assertEquals("Emri i hotelit eshte i detyrueshem", exception.getMessage());
    }

    // ========== TESTET PËR deleteHotel ==========

    @Test
    void testDeleteHotel_WhenHotelExists_DeletesSuccessfully() {
        // GIVEN
        int hotelId = 1;

        when(hotelRepository.existsById(hotelId))
                .thenReturn(true);

        doNothing().when(hotelRepository).deleteById(hotelId);

        // WHEN
        hotelService.deleteHotel(hotelId);

        // THEN
        verify(hotelRepository, times(1)).existsById(hotelId);
        verify(hotelRepository, times(1)).deleteById(hotelId);
    }

    @Test
    void testDeleteHotel_WhenHotelDoesNotExist_ThrowsException() {
        // GIVEN
        int hotelId = 999;

        when(hotelRepository.existsById(hotelId))
                .thenReturn(false);

        // WHEN & THEN
        ResourceNotFindException exception = assertThrows(
                ResourceNotFindException.class,
                () -> hotelService.deleteHotel(hotelId)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(hotelId)));
        verify(hotelRepository, times(1)).existsById(hotelId);
        verify(hotelRepository, never()).deleteById(anyInt());
    }

    // ========== TESTET PËR getAllHotels ==========

    @Test
    void testGetAllHotels_ReturnsListOfHotels() {
        // GIVEN
        Hotel hotel2 = new Hotel();
        hotel2.setId(2);
        hotel2.setHotelName("Beach Hotel");
        hotel2.setHotelCity("Sarande");

        HotelDto hotelDto2 = new HotelDto(
                2, "Beach Hotel", "Sarande", "Beach Road",
                "+355693333333", 123, "Beach hotel",
                LocalDate.now(), LocalDate.now()
        );

        List<Hotel> hotels = Arrays.asList(hotel, hotel2);

        when(hotelRepository.findAll())
                .thenReturn(hotels);

        when(hotelDtoMapper.apply(hotel))
                .thenReturn(hotelDto);

        when(hotelDtoMapper.apply(hotel2))
                .thenReturn(hotelDto2);

        // WHEN
        List<HotelDto> result = hotelService.getAllHotels();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Grand Hotel", result.get(0).getHotelName());
        assertEquals("Beach Hotel", result.get(1).getHotelName());

        verify(hotelRepository, times(1)).findAll();
        verify(hotelDtoMapper, times(2)).apply(any(Hotel.class));
    }

    @Test
    void testGetAllHotels_WhenNoHotels_ReturnsEmptyList() {
        // GIVEN
        when(hotelRepository.findAll())
                .thenReturn(Arrays.asList());

        // WHEN
        List<HotelDto> result = hotelService.getAllHotels();

        // THEN
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(hotelRepository, times(1)).findAll();
    }

    // ========== TESTET PËR getHotelsByCity ==========

    @Test
    void testGetHotelsByCity_ReturnsHotelsInCity() {
        // GIVEN
        String city = "Tirane";

        Hotel hotel2 = new Hotel();
        hotel2.setId(2);
        hotel2.setHotelName("City Hotel");
        hotel2.setHotelCity("Tirane");

        HotelDto hotelDto2 = new HotelDto(
                2, "City Hotel", "Tirane", "City Center",
                "+355694444444", 123, "City hotel",
                LocalDate.now(), LocalDate.now()
        );

        List<Hotel> hotelsInCity = Arrays.asList(hotel, hotel2);

        when(hotelRepository.getByHotelCity(city))
                .thenReturn(hotelsInCity);

        when(hotelDtoMapper.apply(hotel))
                .thenReturn(hotelDto);

        when(hotelDtoMapper.apply(hotel2))
                .thenReturn(hotelDto2);

        // WHEN
        List<HotelDto> result = hotelService.getHotelsByCity(city);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tirane", result.get(0).getHotelCity());
        assertEquals("Tirane", result.get(1).getHotelCity());

        verify(hotelRepository, times(1)).getByHotelCity(city);
        verify(hotelDtoMapper, times(2)).apply(any(Hotel.class));
    }

    @Test
    void testGetHotelsByCity_WhenNoCityHotels_ReturnsEmptyList() {
        // GIVEN
        String city = "Vlore";

        when(hotelRepository.getByHotelCity(city))
                .thenReturn(Arrays.asList());

        // WHEN
        List<HotelDto> result = hotelService.getHotelsByCity(city);

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hotelRepository, times(1)).getByHotelCity(city);
    }
}



