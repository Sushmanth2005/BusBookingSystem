package com.busease.service;

import com.busease.entity.Booking;
import com.busease.entity.Schedule;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.busease.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final BookingRepository bookingRepository;

    public byte[] generateTicketPdf(String userEmail, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized to download this ticket");
        }

        if (booking.getStatus().name().equals("CANCELLED")) {
            throw new RuntimeException("Cannot download ticket for a cancelled booking");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A5.rotate());
            doc.setMargins(20, 30, 20, 30);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            DeviceRgb brandColor = new DeviceRgb(67, 56, 202); // indigo-700
            DeviceRgb lightGray = new DeviceRgb(241, 245, 249);

            Schedule schedule = booking.getSchedule();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

            // Header
            doc.add(new Paragraph("BusEase")
                    .setFont(bold).setFontSize(24).setFontColor(brandColor)
                    .setMarginBottom(2));
            doc.add(new Paragraph("E-Ticket / Boarding Pass")
                    .setFont(regular).setFontSize(10).setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(15));

            // Booking ID badge
            doc.add(new Paragraph("BOOKING #" + String.format("%06d", booking.getId()))
                    .setFont(bold).setFontSize(12)
                    .setBackgroundColor(lightGray)
                    .setPadding(8)
                    .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(4))
                    .setMarginBottom(12));

            // Journey details table
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            addRow(table, "Passenger", booking.getPassengerName() != null ? booking.getPassengerName() : booking.getUser().getName(), bold, regular);
            addRow(table, "From", schedule.getRoute().getSource(), bold, regular);
            addRow(table, "To", schedule.getRoute().getDestination(), bold, regular);
            addRow(table, "Bus", schedule.getBus().getBusName(), bold, regular);
            addRow(table, "Departure", schedule.getDepartureTime().format(dtf), bold, regular);
            addRow(table, "Arrival", schedule.getArrivalTime().format(dtf), bold, regular);
            addRow(table, "Seats", String.join(", ", Arrays.asList(booking.getSeatNumbers().split(","))), bold, regular);
            addRow(table, "Amount Paid", "₹" + String.format("%.2f", booking.getTotalAmount()), bold, regular);
            addRow(table, "Status", booking.getStatus().name(), bold, regular);
            addRow(table, "Booked On", booking.getBookingDate().format(dtf), bold, regular);

            if (booking.getPassengerPhone() != null) {
                addRow(table, "Phone", booking.getPassengerPhone(), bold, regular);
            }

            doc.add(table);

            // Footer
            doc.add(new Paragraph("This is a computer-generated ticket. No signature required.")
                    .setFont(regular).setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(15));
            doc.add(new Paragraph("Thank you for traveling with BusEase!")
                    .setFont(bold).setFontSize(10).setFontColor(brandColor)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF ticket: " + e.getMessage(), e);
        }
    }

    private void addRow(Table table, String label, String value, PdfFont bold, PdfFont regular) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(bold).setFontSize(9).setFontColor(ColorConstants.GRAY))
                .setBorder(Border.NO_BORDER).setPaddingBottom(4));
        table.addCell(new Cell().add(new Paragraph(value).setFont(regular).setFontSize(10))
                .setBorder(Border.NO_BORDER).setPaddingBottom(4)
                .setBorderBottom(new SolidBorder(new DeviceRgb(226, 232, 240), 0.5f)));
    }
}
