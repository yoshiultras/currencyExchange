package com.example.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseGenerator {
    HttpServletRequest request;
    HttpServletResponse response;

    public ResponseGenerator(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    public void notValidCurrency() throws IOException {
        response.sendError(400, "Not a valid currency. Should be like this: USD, US DOLLAR, $.");
    }
    public void currencyExists() throws IOException {
        response.sendError(409, "This currency already exists.");
    }
    public void generalException() throws IOException {
        response.sendError(500, "Something wrong on our side.");
    }
}