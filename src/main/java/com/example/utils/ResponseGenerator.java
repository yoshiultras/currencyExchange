package com.example.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseGenerator {
    HttpServletResponse response;

    public ResponseGenerator(HttpServletResponse response) {
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
    public void currencyNotExists() throws IOException {
        response.sendError(404, "This currency does not exist.");
    }

    public void rateNotExists() throws IOException {
        response.sendError(404, "This exchange rate does not exist.");
    }

    public void invalidRate() throws IOException {
        response.sendError(400, "Invalid rate. Should be like this: 0.4");
    }

    public void rateExists() throws IOException {
        response.sendError(409, "This exchange rate already exist.");
    }

    public void invalidAmount() throws IOException {
        response.sendError(400, "Invalid amount. Should look like this: 100.0 or 100");
    }

    public void invalidCode() throws IOException {
        response.sendError(400, "Invalid currency code. Should look like this: USD, RUB");
    }

}
