package com.example.servlets;

import com.example.models.ExchangeRate;
import com.example.repositories.CurrencyRepository;
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
    CurrencyRepository currencyRepository;

    @Override
    public void init() {
        try {
            exchangeRateRepository = new ExchangeRateRepository();
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");

        try {
            List<ExchangeRate> list = exchangeRateRepository.getRates();
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            for (ExchangeRate exchangeRate : list) {
                out.print(gson.toJson(exchangeRate));
            }
            out.flush();
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            String baseCode = request.getParameter("baseCurrencyCode");
            String targetCode = request.getParameter("targetCurrencyCode");
            double rate = Double.parseDouble(request.getParameter("rate"));
            if (baseCode.length() != 3 || targetCode.length() != 3) {
                responseGenerator.invalidCode();
                return;
            }
            if (!currencyRepository.exists(baseCode) || !currencyRepository.exists(targetCode)) {
                responseGenerator.currencyNotExists();
                return;
            }
            if (exchangeRateRepository.exists(baseCode, targetCode)) {
                responseGenerator.rateExists();
                return;
            }

            ExchangeRate exchangeRate = exchangeRateRepository.addRate(baseCode, targetCode, rate);

            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(exchangeRate));
            out.flush();
        } catch (SQLException e) {
            System.out.println(e);
            responseGenerator.generalException();
        } catch (Exception e) {
            responseGenerator.invalidRate();
        }
    }
}
