package com.csc340.glowup.service;

import com.csc340.glowup.dto.AdminMonthlyStats;
import com.csc340.glowup.dto.AdminStatsResponse;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import com.csc340.glowup.enums.AppointmentStatus;
import com.csc340.glowup.repository.AdminAppointmentRepository;
import com.csc340.glowup.repository.AdminServiceRepository;
import com.csc340.glowup.repository.AdminUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminStatsService {

    static final ZoneId REPORT_ZONE = ZoneId.of("America/New_York");
    private static final int DEFAULT_MONTHS = 6;
    private static final long MAX_DAYS = 731;

    private final AdminAccess adminAccess;
    private final AdminUserRepository adminUserRepository;
    private final AdminServiceRepository adminServiceRepository;
    private final AdminAppointmentRepository adminAppointmentRepository;

    public AdminStatsService(
        AdminAccess adminAccess,
        AdminUserRepository adminUserRepository,
        AdminServiceRepository adminServiceRepository,
        AdminAppointmentRepository adminAppointmentRepository) {
        this.adminAccess = adminAccess;
        this.adminUserRepository = adminUserRepository;
        this.adminServiceRepository = adminServiceRepository;
        this.adminAppointmentRepository = adminAppointmentRepository;
    }

    /** from/to are optional; the default is the last 6 months including the current one. */
    public AdminStatsResponse getStats(Long adminId, LocalDate from, LocalDate to) {
        adminAccess.requireAdmin(adminId);
        LocalDate today = LocalDate.now(REPORT_ZONE);
        LocalDate end = to != null ? to : today;
        LocalDate start = from != null ? from : end.withDayOfMonth(1).minusMonths(DEFAULT_MONTHS - 1);
        if (start.isAfter(end)) {
            throw new BadRequestException("from must be on or before to.");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_DAYS) {
            throw new BadRequestException("The reporting period can be at most 24 months.");
        }

        // [start of first day, start of the day after the last day) in the report zone
        OffsetDateTime rangeStart = start.atStartOfDay(REPORT_ZONE).toOffsetDateTime();
        OffsetDateTime rangeEnd = end.plusDays(1).atStartOfDay(REPORT_ZONE).toOffsetDateTime();

        Map<YearMonth, long[]> monthly = emptyMonths(start, end); // [newUsers, servicesSubmitted, completed]
        long newCustomers = 0;
        long newProviders = 0;
        for (Object[] row : adminUserRepository.findSignupsBetween(rangeStart, rangeEnd, AccountRole.ADMIN)) {
            AccountRole role = (AccountRole) row[0];
            OffsetDateTime createdAt = (OffsetDateTime) row[1];
            if (role == AccountRole.CUSTOMER) {
                newCustomers++;
            } else if (role == AccountRole.PROVIDER) {
                newProviders++;
            }
            monthly.get(YearMonth.from(createdAt.atZoneSameInstant(REPORT_ZONE)))[0]++;
        }

        List<OffsetDateTime> submissions = adminServiceRepository.findSubmissionTimesBetween(rangeStart, rangeEnd);
        for (OffsetDateTime createdAt : submissions) {
            monthly.get(YearMonth.from(createdAt.atZoneSameInstant(REPORT_ZONE)))[1]++;
        }

        // completed = COMPLETED, or CONFIRMED and ended (see Appointment.isCompleted()); counted in the month it ended
        LocalDateTime periodStart = start.atStartOfDay();
        LocalDateTime periodEnd = end.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now(REPORT_ZONE);
        long completed = 0;
        // start one day early so an appointment that started before the period but ended in it is included
        for (Object[] row : adminAppointmentRepository.findStartsAndDurations(
                List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED), periodStart.minusDays(1), periodEnd)) {
            LocalDateTime endsAt = ((LocalDateTime) row[0]).plusMinutes((Integer) row[1]);
            if (!endsAt.isBefore(periodStart) && endsAt.isBefore(periodEnd) && endsAt.isBefore(now)) {
                completed++;
                monthly.get(YearMonth.from(endsAt))[2]++;
            }
        }

        List<AdminMonthlyStats> months = new ArrayList<>();
        monthly.forEach((month, counts) ->
                months.add(new AdminMonthlyStats(month.toString(), counts[2], counts[0], counts[1])));

        return new AdminStatsResponse(
                start, end, REPORT_ZONE.getId(),
                completed,
                newCustomers + newProviders, newCustomers, newProviders,
                adminUserRepository.countByAccountRoleAndAccountStatus(AccountRole.PROVIDER, AccountStatus.ACTIVE),
                adminUserRepository.countByAccountRoleAndAccountStatus(AccountRole.PROVIDER, AccountStatus.PENDING_APPROVAL),
                submissions.size(),
                months);
    }

    private static Map<YearMonth, long[]> emptyMonths(LocalDate start, LocalDate end) {
        Map<YearMonth, long[]> months = new LinkedHashMap<>();
        for (YearMonth m = YearMonth.from(start); !m.isAfter(YearMonth.from(end)); m = m.plusMonths(1)) {
            months.put(m, new long[3]);
        }
        return months;
    }
}
