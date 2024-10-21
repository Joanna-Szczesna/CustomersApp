package pl.szczesnaj.customersapp.controller;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.net.URI;

record CustomerErrorResponse(String message) implements ErrorResponse {

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public ProblemDetail getBody() {
        ProblemDetail details = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        details.setStatus(HttpStatus.NOT_FOUND);
        details.setDetail(message);
        return details;
    }
}
