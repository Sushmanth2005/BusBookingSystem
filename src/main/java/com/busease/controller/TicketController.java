package com.busease.controller;

import com.busease.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> downloadTicket(Authentication auth, @PathVariable Long id) {
        byte[] pdfBytes = ticketService.generateTicketPdf(auth.getName(), id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "BusEase-Ticket-" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
