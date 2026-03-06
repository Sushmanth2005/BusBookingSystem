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
        log.info("Checking database for test data initialization...");

        // ===== 1. CREATE USERS =====
        if (userRepository.count() == 0) {
            log.info("No users found. Creating test accounts...");
            userRepository.save(User.builder()
                    .name("Admin User")
                    .email("admin@busease.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());

            userRepository.save(User.builder()
                    .name("Test User")
                    .email("user@busease.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build());

            userRepository.save(User.builder()
                    .name("Ravi Kumar")
                    .email("ravi@busease.com")
                    .password(passwordEncoder.encode("ravi123"))
                    .role(Role.USER)
                    .build());
        }

        if (busService.getAllBuses().size() >= 30) {
            log.info("Database already contains the 30 test buses. Skipping bus/schedule initialization.");
            return;
        }

        log.info("Adding 30 test buses, routes, and schedules...");

        // ===== 2. CREATE BUSES (30 buses) =====
        // AC Sleeper (premium, 40 seats)
        var b1  = busService.addBus(new BusRequest("APSRTC Garuda Plus",      40, BusType.AC));
        var b2  = busService.addBus(new BusRequest("TSRTC Rajdhani",          40, BusType.AC));
        var b3  = busService.addBus(new BusRequest("Orange Travels Sleeper",  40, BusType.AC));
        var b4  = busService.addBus(new BusRequest("SRS Travels AC",          40, BusType.AC));
        var b5  = busService.addBus(new BusRequest("VRL Travels Multi-Axle",  40, BusType.AC));
        var b6  = busService.addBus(new BusRequest("KPN Travels Volvo",       40, BusType.AC));
        var b7  = busService.addBus(new BusRequest("Kaveri Travels Premium",  40, BusType.AC));
        var b8  = busService.addBus(new BusRequest("National Travels Deluxe", 40, BusType.AC));
        var b9  = busService.addBus(new BusRequest("Jabbar Travels AC",       40, BusType.AC));
        var b10 = busService.addBus(new BusRequest("IntrCity SmartBus",       40, BusType.AC));
        var b11 = busService.addBus(new BusRequest("Greenline Travels AC",    40, BusType.AC));
        var b12 = busService.addBus(new BusRequest("Kesineni Travels Volvo",  40, BusType.AC));
        var b13 = busService.addBus(new BusRequest("Sharma Transports AC",    40, BusType.AC));
        var b14 = busService.addBus(new BusRequest("Eagle Travels AC",        40, BusType.AC));
        var b15 = busService.addBus(new BusRequest("RedBus Express AC",       40, BusType.AC));

        // NON-AC Seater (economy, 30 seats)
        var b16 = busService.addBus(new BusRequest("APSRTC Express",          30, BusType.NON_AC));
        var b17 = busService.addBus(new BusRequest("TSRTC Express",           30, BusType.NON_AC));
        var b18 = busService.addBus(new BusRequest("Parveen Travels",         30, BusType.NON_AC));
        var b19 = busService.addBus(new BusRequest("SVR Travels",             30, BusType.NON_AC));
        var b20 = busService.addBus(new BusRequest("Durgamba Travels",        30, BusType.NON_AC));
        var b21 = busService.addBus(new BusRequest("Amaravathi Travels",      30, BusType.NON_AC));
        var b22 = busService.addBus(new BusRequest("Diwakar Travels",         30, BusType.NON_AC));
        var b23 = busService.addBus(new BusRequest("Morning Star Travels",    30, BusType.NON_AC));
        var b24 = busService.addBus(new BusRequest("Mahalaxmi Travels",       30, BusType.NON_AC));
        var b25 = busService.addBus(new BusRequest("Ramoji Travels",          30, BusType.NON_AC));
        var b26 = busService.addBus(new BusRequest("Yatra Travels",           30, BusType.NON_AC));
        var b27 = busService.addBus(new BusRequest("Sai Anjali Travels",      30, BusType.NON_AC));
        var b28 = busService.addBus(new BusRequest("Kallada Travels",         30, BusType.NON_AC));
        var b29 = busService.addBus(new BusRequest("Paulo Travels",           30, BusType.NON_AC));
        var b30 = busService.addBus(new BusRequest("Hans Travels",            30, BusType.NON_AC));

        // ===== 3. CREATE ROUTES (15 city pairs × 2 directions = 30 routes) =====
        // Pair 1: Hyderabad ↔ Vijayawada
        var r1  = routeService.addRoute(new RouteRequest("Hyderabad",      "Vijayawada",      275.0));
        var r2  = routeService.addRoute(new RouteRequest("Vijayawada",     "Hyderabad",       275.0));
        // Pair 2: Hyderabad ↔ Bangalore
        var r3  = routeService.addRoute(new RouteRequest("Hyderabad",      "Bangalore",       570.0));
        var r4  = routeService.addRoute(new RouteRequest("Bangalore",      "Hyderabad",       570.0));
        // Pair 3: Hyderabad ↔ Chennai
        var r5  = routeService.addRoute(new RouteRequest("Hyderabad",      "Chennai",         625.0));
        var r6  = routeService.addRoute(new RouteRequest("Chennai",        "Hyderabad",       625.0));
        // Pair 4: Vijayawada ↔ Visakhapatnam
        var r7  = routeService.addRoute(new RouteRequest("Vijayawada",     "Visakhapatnam",   350.0));
        var r8  = routeService.addRoute(new RouteRequest("Visakhapatnam",  "Vijayawada",      350.0));
        // Pair 5: Hyderabad ↔ Tirupati
        var r9  = routeService.addRoute(new RouteRequest("Hyderabad",      "Tirupati",        550.0));
        var r10 = routeService.addRoute(new RouteRequest("Tirupati",       "Hyderabad",       550.0));
        // Pair 6: Chennai ↔ Bangalore
        var r11 = routeService.addRoute(new RouteRequest("Chennai",        "Bangalore",       345.0));
        var r12 = routeService.addRoute(new RouteRequest("Bangalore",      "Chennai",         345.0));
        // Pair 7: Mumbai ↔ Pune
        var r13 = routeService.addRoute(new RouteRequest("Mumbai",         "Pune",            150.0));
        var r14 = routeService.addRoute(new RouteRequest("Pune",           "Mumbai",          150.0));
        // Pair 8: Hyderabad ↔ Mumbai
        var r15 = routeService.addRoute(new RouteRequest("Hyderabad",      "Mumbai",          710.0));
        var r16 = routeService.addRoute(new RouteRequest("Mumbai",         "Hyderabad",       710.0));
        // Pair 9: Delhi ↔ Mumbai
        var r17 = routeService.addRoute(new RouteRequest("Delhi",          "Mumbai",         1400.0));
        var r18 = routeService.addRoute(new RouteRequest("Mumbai",         "Delhi",          1400.0));
        // Pair 10: Bangalore ↔ Visakhapatnam
        var r19 = routeService.addRoute(new RouteRequest("Bangalore",      "Visakhapatnam",   800.0));
        var r20 = routeService.addRoute(new RouteRequest("Visakhapatnam",  "Bangalore",       800.0));
        // Pair 11: Vijayawada ↔ Chennai
        var r21 = routeService.addRoute(new RouteRequest("Vijayawada",     "Chennai",         430.0));
        var r22 = routeService.addRoute(new RouteRequest("Chennai",        "Vijayawada",      430.0));
        // Pair 12: Hyderabad ↔ Kolkata
        var r23 = routeService.addRoute(new RouteRequest("Hyderabad",      "Kolkata",        1500.0));
        var r24 = routeService.addRoute(new RouteRequest("Kolkata",        "Hyderabad",      1500.0));
        // Pair 13: Bangalore ↔ Pune
        var r25 = routeService.addRoute(new RouteRequest("Bangalore",      "Pune",            840.0));
        var r26 = routeService.addRoute(new RouteRequest("Pune",           "Bangalore",       840.0));
        // Pair 14: Delhi ↔ Kolkata
        var r27 = routeService.addRoute(new RouteRequest("Delhi",          "Kolkata",        1530.0));
        var r28 = routeService.addRoute(new RouteRequest("Kolkata",        "Delhi",          1530.0));
        // Pair 15: Tirupati ↔ Chennai
        var r29 = routeService.addRoute(new RouteRequest("Tirupati",       "Chennai",         135.0));
        var r30 = routeService.addRoute(new RouteRequest("Chennai",        "Tirupati",        135.0));

        // ===== 4. CREATE SCHEDULES (multiple days, multiple buses per route) =====
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayAfter = tomorrow.plusDays(1);
        LocalDateTime day3     = tomorrow.plusDays(2);
        LocalDateTime day4     = tomorrow.plusDays(3);

        // --- Hyderabad ↔ Vijayawada (4.5 hrs) — 6 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b1.getId(),  r1.getId(),  tomorrow.withHour(6),   tomorrow.withHour(10).withMinute(30),   450.0));
        scheduleService.addSchedule(new ScheduleRequest(b16.getId(), r1.getId(),  tomorrow.withHour(14),  tomorrow.withHour(18).withMinute(30),   300.0));
        scheduleService.addSchedule(new ScheduleRequest(b11.getId(), r1.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(2).withMinute(30), 550.0));
        scheduleService.addSchedule(new ScheduleRequest(b2.getId(),  r2.getId(),  tomorrow.withHour(7),   tomorrow.withHour(11).withMinute(30),   450.0));
        scheduleService.addSchedule(new ScheduleRequest(b17.getId(), r2.getId(),  tomorrow.withHour(15),  tomorrow.withHour(19).withMinute(30),   300.0));
        scheduleService.addSchedule(new ScheduleRequest(b12.getId(), r2.getId(),  tomorrow.withHour(23),  tomorrow.plusDays(1).withHour(3).withMinute(30), 550.0));

        // --- Hyderabad ↔ Bangalore (8 hrs) — 6 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b3.getId(),  r3.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5),  1200.0));
        scheduleService.addSchedule(new ScheduleRequest(b18.getId(), r3.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(4),   800.0));
        scheduleService.addSchedule(new ScheduleRequest(b13.getId(), r3.getId(),  dayAfter.withHour(22),  dayAfter.plusDays(1).withHour(6),  1250.0));
        scheduleService.addSchedule(new ScheduleRequest(b4.getId(),  r4.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(6),  1200.0));
        scheduleService.addSchedule(new ScheduleRequest(b19.getId(), r4.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5),   800.0));
        scheduleService.addSchedule(new ScheduleRequest(b14.getId(), r4.getId(),  dayAfter.withHour(21),  dayAfter.plusDays(1).withHour(5),  1250.0));

        // --- Hyderabad ↔ Chennai (9 hrs) — 6 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b5.getId(),  r5.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(5),  1400.0));
        scheduleService.addSchedule(new ScheduleRequest(b20.getId(), r5.getId(),  tomorrow.withHour(19),  tomorrow.plusDays(1).withHour(4),   900.0));
        scheduleService.addSchedule(new ScheduleRequest(b15.getId(), r5.getId(),  dayAfter.withHour(20),  dayAfter.plusDays(1).withHour(5),  1450.0));
        scheduleService.addSchedule(new ScheduleRequest(b6.getId(),  r6.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(6),  1400.0));
        scheduleService.addSchedule(new ScheduleRequest(b21.getId(), r6.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(5),   900.0));
        scheduleService.addSchedule(new ScheduleRequest(b6.getId(),  r6.getId(),  dayAfter.withHour(21),  dayAfter.plusDays(1).withHour(6),  1450.0));

        // --- Vijayawada ↔ Visakhapatnam (6 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b7.getId(),  r7.getId(),  tomorrow.withHour(8),   tomorrow.withHour(14),  750.0));
        scheduleService.addSchedule(new ScheduleRequest(b22.getId(), r7.getId(),  tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(4),  500.0));
        scheduleService.addSchedule(new ScheduleRequest(b8.getId(),  r8.getId(),  tomorrow.withHour(9),   tomorrow.withHour(15),  750.0));
        scheduleService.addSchedule(new ScheduleRequest(b23.getId(), r8.getId(),  tomorrow.withHour(23),  tomorrow.plusDays(1).withHour(5),  500.0));

        // --- Hyderabad ↔ Tirupati (8.5 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b9.getId(),  r9.getId(),  tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5).withMinute(30),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(b24.getId(), r9.getId(),  tomorrow.withHour(20),  tomorrow.plusDays(1).withHour(4).withMinute(30),   700.0));
        scheduleService.addSchedule(new ScheduleRequest(b10.getId(), r10.getId(), tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(6).withMinute(30),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(b25.getId(), r10.getId(), tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(5).withMinute(30),   700.0));

        // --- Chennai ↔ Bangalore (6 hrs) — 6 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b1.getId(),  r11.getId(), dayAfter.withHour(7),   dayAfter.withHour(13),  900.0));
        scheduleService.addSchedule(new ScheduleRequest(b16.getId(), r11.getId(), dayAfter.withHour(14),  dayAfter.withHour(20),  600.0));
        scheduleService.addSchedule(new ScheduleRequest(b11.getId(), r11.getId(), dayAfter.withHour(23),  dayAfter.plusDays(1).withHour(5),  950.0));
        scheduleService.addSchedule(new ScheduleRequest(b2.getId(),  r12.getId(), dayAfter.withHour(8),   dayAfter.withHour(14),  900.0));
        scheduleService.addSchedule(new ScheduleRequest(b17.getId(), r12.getId(), dayAfter.withHour(15),  dayAfter.withHour(21),  600.0));
        scheduleService.addSchedule(new ScheduleRequest(b12.getId(), r12.getId(), dayAfter.withHour(22),  dayAfter.plusDays(1).withHour(4),  950.0));

        // --- Mumbai ↔ Pune (3 hrs) — 8 schedules (popular route, many departures) ---
        scheduleService.addSchedule(new ScheduleRequest(b3.getId(),  r13.getId(), tomorrow.withHour(6),   tomorrow.withHour(9),   500.0));
        scheduleService.addSchedule(new ScheduleRequest(b18.getId(), r13.getId(), tomorrow.withHour(10),  tomorrow.withHour(13),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(b13.getId(), r13.getId(), tomorrow.withHour(14),  tomorrow.withHour(17),  500.0));
        scheduleService.addSchedule(new ScheduleRequest(b26.getId(), r13.getId(), tomorrow.withHour(18),  tomorrow.withHour(21),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(b4.getId(),  r14.getId(), tomorrow.withHour(7),   tomorrow.withHour(10),  500.0));
        scheduleService.addSchedule(new ScheduleRequest(b19.getId(), r14.getId(), tomorrow.withHour(11),  tomorrow.withHour(14),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(b14.getId(), r14.getId(), tomorrow.withHour(15),  tomorrow.withHour(18),  500.0));
        scheduleService.addSchedule(new ScheduleRequest(b27.getId(), r14.getId(), tomorrow.withHour(19),  tomorrow.withHour(22),  350.0));

        // --- Hyderabad ↔ Mumbai (11 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b5.getId(),  r15.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(5),  1600.0));
        scheduleService.addSchedule(new ScheduleRequest(b20.getId(), r15.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(6),  1000.0));
        scheduleService.addSchedule(new ScheduleRequest(b6.getId(),  r16.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(6),  1600.0));
        scheduleService.addSchedule(new ScheduleRequest(b21.getId(), r16.getId(), dayAfter.withHour(20),  dayAfter.plusDays(1).withHour(7),  1000.0));

        // --- Delhi ↔ Mumbai (18 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b7.getId(),  r17.getId(), day3.withHour(14),  day3.plusDays(1).withHour(8),  2500.0));
        scheduleService.addSchedule(new ScheduleRequest(b22.getId(), r17.getId(), day3.withHour(16),  day3.plusDays(1).withHour(10), 1500.0));
        scheduleService.addSchedule(new ScheduleRequest(b8.getId(),  r18.getId(), day3.withHour(15),  day3.plusDays(1).withHour(9),  2500.0));
        scheduleService.addSchedule(new ScheduleRequest(b23.getId(), r18.getId(), day3.withHour(17),  day3.plusDays(1).withHour(11), 1500.0));

        // --- Bangalore ↔ Visakhapatnam (12 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b9.getId(),  r19.getId(), dayAfter.withHour(17),  dayAfter.plusDays(1).withHour(5),  1800.0));
        scheduleService.addSchedule(new ScheduleRequest(b24.getId(), r19.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(6),  1100.0));
        scheduleService.addSchedule(new ScheduleRequest(b10.getId(), r20.getId(), dayAfter.withHour(18),  dayAfter.plusDays(1).withHour(6),  1800.0));
        scheduleService.addSchedule(new ScheduleRequest(b25.getId(), r20.getId(), dayAfter.withHour(19),  dayAfter.plusDays(1).withHour(7),  1100.0));

        // --- Vijayawada ↔ Chennai (7 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b11.getId(), r21.getId(), tomorrow.withHour(8),   tomorrow.withHour(15),   800.0));
        scheduleService.addSchedule(new ScheduleRequest(b26.getId(), r21.getId(), tomorrow.withHour(21),  tomorrow.plusDays(1).withHour(4),  550.0));
        scheduleService.addSchedule(new ScheduleRequest(b12.getId(), r22.getId(), tomorrow.withHour(9),   tomorrow.withHour(16),   800.0));
        scheduleService.addSchedule(new ScheduleRequest(b27.getId(), r22.getId(), tomorrow.withHour(22),  tomorrow.plusDays(1).withHour(5),  550.0));

        // --- Hyderabad ↔ Kolkata (20 hrs) — 2 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b15.getId(), r23.getId(), day3.withHour(10),  day3.plusDays(1).withHour(6),  2800.0));
        scheduleService.addSchedule(new ScheduleRequest(b28.getId(), r24.getId(), day3.withHour(11),  day3.plusDays(1).withHour(7),  1800.0));

        // --- Bangalore ↔ Pune (13 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b13.getId(), r25.getId(), day4.withHour(18),  day4.plusDays(1).withHour(7),  1500.0));
        scheduleService.addSchedule(new ScheduleRequest(b29.getId(), r25.getId(), day4.withHour(20),  day4.plusDays(1).withHour(9),   950.0));
        scheduleService.addSchedule(new ScheduleRequest(b14.getId(), r26.getId(), day4.withHour(19),  day4.plusDays(1).withHour(8),  1500.0));
        scheduleService.addSchedule(new ScheduleRequest(b30.getId(), r26.getId(), day4.withHour(21),  day4.plusDays(1).withHour(10),  950.0));

        // --- Delhi ↔ Kolkata (20 hrs) — 2 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b15.getId(), r27.getId(), day4.withHour(12),  day4.plusDays(1).withHour(8),  2600.0));
        scheduleService.addSchedule(new ScheduleRequest(b28.getId(), r28.getId(), day4.withHour(13),  day4.plusDays(1).withHour(9),  1700.0));

        // --- Tirupati ↔ Chennai (2.5 hrs) — 4 schedules ---
        scheduleService.addSchedule(new ScheduleRequest(b1.getId(),  r29.getId(), tomorrow.withHour(6),   tomorrow.withHour(8).withMinute(30),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(b16.getId(), r29.getId(), tomorrow.withHour(12),  tomorrow.withHour(14).withMinute(30), 220.0));
        scheduleService.addSchedule(new ScheduleRequest(b2.getId(),  r30.getId(), tomorrow.withHour(7),   tomorrow.withHour(9).withMinute(30),  350.0));
        scheduleService.addSchedule(new ScheduleRequest(b17.getId(), r30.getId(), tomorrow.withHour(13),  tomorrow.withHour(15).withMinute(30), 220.0));

        log.info("============================================================");
        log.info("  TEST DATA INITIALIZED SUCCESSFULLY");
        log.info("============================================================");
        log.info("  CITIES    : 10 (Hyderabad, Vijayawada, Visakhapatnam,");
        log.info("               Chennai, Bangalore, Tirupati, Mumbai,");
        log.info("               Delhi, Pune, Kolkata)");
        log.info("  BUSES     : 30 (15 AC + 15 Non-AC)");
        log.info("  ROUTES    : 30 (15 city pairs × 2 directions)");
        log.info("  SCHEDULES : 68 (multi-day, multi-departure)");
        log.info("------------------------------------------------------------");
        log.info("  LOGIN: admin@busease.com / admin123");
        log.info("============================================================");
    }
}
