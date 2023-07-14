package com.example.servlets;

import com.example.models.ExchangeRate;
import com.example.repositories.ExchangeRateRepository;
import com.example.utils.ResponseGenerator;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    ExchangeRateRepository exchangeRateRepository;

    @Override
    public void init() {
        try {
            exchangeRateRepository = new ExchangeRateRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        try {
            List<ExchangeRate> list = exchangeRateRepository.getRates();
            for (ExchangeRate exchangeRate : list) {
                out.print(gson.toJson(exchangeRate));
            }
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
