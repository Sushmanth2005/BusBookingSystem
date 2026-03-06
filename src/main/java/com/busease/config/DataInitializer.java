package com.busease.config;

import com.busease.dto.BusRequest;
import com.busease.dto.RouteRequest;
import com.busease.dto.ScheduleRequest;
import com.busease.entity.User;
import com.busease.enums.BusType;
import com.busease.enums.Role;
import com.busease.repository.UserRepository;
import com.busease.service.BusService;
import com.busease.service.RouteService;
import com.busease.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusService busService;
    private final RouteService routeService;
    private final ScheduleService scheduleService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initialization.");
            return;
        }

        log.info("Database is empty. Initializing test data...");

        // ===== 1. CREATE USERS =====
        User admin = User.builder()
                .name("Admin User")
                .email("admin@busease.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .name("Test User")
                .email("user@busease.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .name("Ravi Kumar")
                .email("ravi@busease.com")
                .password(passwordEncoder.encode("ravi123"))
                .role(Role.USER)
                .build();
        userRepository.save(user2);

        // ===== 2. CREATE BUSES (20 buses — 2 per route pair) =====
        // AC Buses (premium, 40 seats)
        var bus1  = busService.addBus(new BusRequest("APSRTC Garuda Plus",   40, BusType.AC));
        var bus2  = busService.addBus(new BusRequest("TSRTC Rajdhani",       40, BusType.AC));
        var bus3  = busService.addBus(new BusRequest("Orange Travels",       40, BusType.AC));
        var bus4  = busService.addBus(new BusRequest("SRS Travels",          40, BusType.AC));
        var bus5  = busService.addBus(new BusRequest("VRL Travels",          40, BusType.AC));
        var bus6  = busService.addBus(new BusRequest("KPN Travels",          40, BusType.AC));
        var bus7  = busService.addBus(new BusRequest("Kaveri Travels",       40, BusType.AC));
        var bus8  = busService.addBus(new BusRequest("National Travels",     40, BusType.AC));
        var bus9  = busService.addBus(new BusRequest("Jabbar Travels",       40, BusType.AC));
        var bus10 = busService.addBus(new BusRequest("IntrCity SmartBus",    40, BusType.AC));

        // NON-AC Buses (economy, 30 seats)
        var bus11 = busService.addBus(new BusRequest("APSRTC Express",       30, BusType.NON_AC));
        var bus12 = busService.addBus(new BusRequest("TSRTC Express",        30, BusType.NON_AC));
        var bus13 = busService.addBus(new BusRequest("Parveen Travels",      30, BusType.NON_AC));
        var bus14 = busService.addBus(new BusRequest("SVR Travels",          30, BusType.NON_AC));
        var bus15 = busService.addBus(new BusRequest("Durgamba Travels",     30, BusType.NON_AC));
        var bus16 = busService.addBus(new BusRequest("Amaravathi Travels",   30, BusType.NON_AC));
        var bus17 = busService.addBus(new BusRequest("Diwakar Travels",      30, BusType.NON_AC));
        var bus18 = busService.addBus(new BusRequest("Morning Star Travels", 30, BusType.NON_AC));
        var bus19 = busService.addBus(new BusRequest("Mahalaxmi Travels",    30, BusType.NON_AC));
        var bus20 = busService.addBus(new BusRequest("Ramoji Travels",       30, BusType.NON_AC));

        // ===== 3. CREATE ROUTES (10 city pairs × 2 directions = 20 routes) =====
        // Cities: Hyderabad, Vijayawada, Visakhapatnam, Chennai, Bangalore, Tirupati,
        //         Mumbai, Delhi, Pune, Kolkata

        // Route pair 1: Hyderabad ↔ Vijayawada (275 km)
        var r1  = routeService.addRoute(new RouteRequest("Hyderabad",      "Vijayawada",      275.0));
        var r2  = routeService.addRoute(new RouteRequest("Vijayawada",     "Hyderabad",       275.0));

        // Route pair 2: Hyderabad ↔ Bangalore (570 km)
        var r3  = routeService.addRoute(new RouteRequest("Hyderabad",      "Bangalore",       570.0));
        var r4  = routeService.addRoute(new RouteRequest("Bangalore",      "Hyderabad",       570.0));

        // Route pair 3: Hyderabad ↔ Chennai (625 km)
        var r5  = routeService.addRoute(new RouteRequest("Hyderabad",      "Chennai",         625.0));
        var r6  = routeService.addRoute(new RouteRequest("Chennai",        "Hyderabad",       625.0));

        // Route pair 4: Vijayawada ↔ Visakhapatnam (350 km)
        var r7  = routeService.addRoute(new RouteRequest("Vijayawada",     "Visakhapatnam",   350.0));
        var r8  = routeService.addRoute(new RouteRequest("Visakhapatnam",  "Vijayawada",      350.0));

        // Route pair 5: Hyderabad ↔ Tirupati (550 km)
        var r9  = routeService.addRoute(new RouteRequest("Hyderabad",      "Tirupati",        550.0));
        var r10 = routeService.addRoute(new RouteRequest("Tirupati",       "Hyderabad",       550.0));

        // Route pair 6: Chennai ↔ Bangalore (345 km)
        var r11 = routeService.addRoute(new RouteRequest("Chennai",        "Bangalore",       345.0));
        var r12 = routeService.addRoute(new RouteRequest("Bangalore",      "Chennai",         345.0));

        // Route pair 7: Mumbai ↔ Pune (150 km)
        var r13 = routeService.addRoute(new RouteRequest("Mumbai",         "Pune",            150.0));
        var r14 = routeService.addRoute(new RouteRequest("Pune",           "Mumbai",          150.0));

        // Route pair 8: Hyderabad ↔ Mumbai (710 km)
        var r15 = routeService.addRoute(new RouteRequest("Hyderabad",      "Mumbai",          710.0));
        var r16 = routeService.addRoute(new RouteRequest("Mumbai",         "Hyderabad",       710.0));

        // Route pair 9: Delhi ↔ Mumbai (1400 km)
        var r17 = routeService.addRoute(new RouteRequest("Delhi",          "Mumbai",         1400.0));
        var r18 = routeService.addRoute(new RouteRequest("Mumbai",         "Delhi",          1400.0));

        // Route pair 10: Bangalore ↔ Visakhapatnam (800 km)
        var r19 = routeService.addRoute(new RouteRequest("Bangalore",      "Visakhapatnam",   800.0));
        var r20 = routeService.addRoute(new RouteRequest("Visakhapatnam",  "Bangalore",       800.0));

        // ===== 4. CREATE SCHEDULES (2 buses per route, multiple departure times) =====
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayAfter = tomorrow.plusDays(1);
        LocalDateTime day3     = tomorrow.plusDays(2);

        // --- Hyderabad ↔ Vijayawada (4.5 hrs, ₹450-600) ---
        scheduleService.addSchedule(new ScheduleRequest(bus1.getId(),  r1.getId(),  tomorrow.withHour(6),   tomorrow.withHour(10).withMinute(30),  450.0));
        scheduleService.addSchedule(new ScheduleRequest(bus11.getId(), r1.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(2).withMinute(30),  300.0));
        scheduleService.addSchedule(new ScheduleRequest(bus2.getId(),  r2.getId(),  tomorrow.withHour(7),   tomorrow.withHour(11).withMinute(30),  450.0));
        scheduleService.addSchedule(new ScheduleRequest(bus12.getId(), r2.getId(),  tomorrow.withHour(23),  tomorrow.plusDays(1).withHour(3).withMinute(30),  300.0));

        // --- Hyderabad ↔ Bangalore (8 hrs, ₹800-1200) ---
        scheduleService.addSchedule(new ScheduleRequest(bus3.getId(),  r3.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5),   1200.0));
        scheduleService.addSchedule(new ScheduleRequest(bus13.getId(), r3.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(4),    800.0));
        scheduleService.addSchedule(new ScheduleRequest(bus4.getId(),  r4.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(6),   1200.0));
        scheduleService.addSchedule(new ScheduleRequest(bus14.getId(), r4.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5),    800.0));

        // --- Hyderabad ↔ Chennai (9 hrs, ₹900-1400) ---
        scheduleService.addSchedule(new ScheduleRequest(bus5.getId(),  r5.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(5),   1400.0));
        scheduleService.addSchedule(new ScheduleRequest(bus15.getId(), r5.getId(),  tomorrow.withHour(19),  tomorrow.plusDays(1).withHour(4),    900.0));
        scheduleService.addSchedule(new ScheduleRequest(bus6.getId(),  r6.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(6),   1400.0));
        scheduleService.addSchedule(new ScheduleRequest(bus16.getId(), r6.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(5),    900.0));

        // --- Vijayawada ↔ Visakhapatnam (6 hrs, ₹500-750) ---
        scheduleService.addSchedule(new ScheduleRequest(bus7.getId(),  r7.getId(),  tomorrow.withHour(8),   tomorrow.withHour(14),  750.0));
        scheduleService.addSchedule(new ScheduleRequest(bus17.getId(), r7.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(4),   500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus8.getId(),  r8.getId(),  tomorrow.withHour(9),   tomorrow.withHour(15),  750.0));
        scheduleService.addSchedule(new ScheduleRequest(bus18.getId(), r8.getId(),  tomorrow.withHour(23),  tomorrow.plusDays(1).withHour(5),   500.0));

        // --- Hyderabad ↔ Tirupati (8.5 hrs, ₹700-1100) ---
        scheduleService.addSchedule(new ScheduleRequest(bus9.getId(),  r9.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5).withMinute(30),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(bus19.getId(), r9.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(4).withMinute(30),   700.0));
        scheduleService.addSchedule(new ScheduleRequest(bus10.getId(), r10.getId(), tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(6).withMinute(30),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(bus20.getId(), r10.getId(), tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5).withMinute(30),   700.0));

        // --- Chennai ↔ Bangalore (6 hrs, ₹600-900) ---
        scheduleService.addSchedule(new ScheduleRequest(bus1.getId(),  r11.getId(), dayAfter.withHour(7),   dayAfter.withHour(13),  900.0));
        scheduleService.addSchedule(new ScheduleRequest(bus11.getId(), r11.getId(), dayAfter.withHour(23),  dayAfter.plusDays(1).withHour(5),   600.0));
        scheduleService.addSchedule(new ScheduleRequest(bus2.getId(),  r12.getId(), dayAfter.withHour(8),   dayAfter.withHour(14),  900.0));
        scheduleService.addSchedule(new ScheduleRequest(bus12.getId(), r12.getId(), dayAfter.withHour(22),  dayAfter.plusDays(1).withHour(4),   600.0));

        // --- Mumbai ↔ Pune (3 hrs, ₹350-500) ---
        scheduleService.addSchedule(new ScheduleRequest(bus3.getId(),  r13.getId(), tomorrow.withHour(6),   tomorrow.withHour(9),   500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus13.getId(), r13.getId(), tomorrow.withHour(14),  tomorrow.withHour(17),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(bus4.getId(),  r14.getId(), tomorrow.withHour(7),   tomorrow.withHour(10),  500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus14.getId(), r14.getId(), tomorrow.withHour(15),  tomorrow.withHour(18),  350.0));

        // --- Hyderabad ↔ Mumbai (11 hrs, ₹1000-1600) ---
        scheduleService.addSchedule(new ScheduleRequest(bus5.getId(),  r15.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(5),  1600.0));
        scheduleService.addSchedule(new ScheduleRequest(bus15.getId(), r15.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(6),  1000.0));
        scheduleService.addSchedule(new ScheduleRequest(bus6.getId(),  r16.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(6),  1600.0));
        scheduleService.addSchedule(new ScheduleRequest(bus16.getId(), r16.getId(), dayAfter.withHour(20),  dayAfter.plusDays(1).withHour(7),  1000.0));

        // --- Delhi ↔ Mumbai (18 hrs, ₹1500-2500) ---
        scheduleService.addSchedule(new ScheduleRequest(bus7.getId(),  r17.getId(), day3.withHour(14),  day3.plusDays(1).withHour(8),  2500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus17.getId(), r17.getId(), day3.withHour(16),  day3.plusDays(1).withHour(10), 1500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus8.getId(),  r18.getId(), day3.withHour(15),  day3.plusDays(1).withHour(9),  2500.0));
        scheduleService.addSchedule(new ScheduleRequest(bus18.getId(), r18.getId(), day3.withHour(17),  day3.plusDays(1).withHour(11), 1500.0));

        // --- Bangalore ↔ Visakhapatnam (12 hrs, ₹1100-1800) ---
        scheduleService.addSchedule(new ScheduleRequest(bus9.getId(),  r19.getId(), dayAfter.withHour(17),  dayAfter.plusDays(1).withHour(5),  1800.0));
        scheduleService.addSchedule(new ScheduleRequest(bus19.getId(), r19.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(6),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(bus10.getId(), r20.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(6),  1800.0));
        scheduleService.addSchedule(new ScheduleRequest(bus20.getId(), r20.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(7),  1100.0));

        log.info("============================================================");
        log.info("  TEST DATA INITIALIZED SUCCESSFULLY");
        log.info("============================================================");
        log.info("  CITIES : Hyderabad, Vijayawada, Visakhapatnam, Chennai,");
        log.info("           Bangalore, Tirupati, Mumbai, Delhi, Pune, Kolkata");
        log.info("  BUSES  : 20 (10 AC + 10 Non-AC)");
        log.info("  ROUTES : 20 (10 city pairs × 2 directions)");
        log.info("  SCHEDULES: 40 (2 per route direction)");
        log.info("------------------------------------------------------------");
        log.info("  LOGIN CREDENTIALS:");
        log.info("  Admin : admin@busease.com / admin123");
        log.info("  User  : user@busease.com  / user123");
        log.info("  User  : ravi@busease.com  / ravi123");
        log.info("============================================================");
    }
}
