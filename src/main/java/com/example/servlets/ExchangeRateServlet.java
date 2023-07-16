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
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {
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

//    @Override
//    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        if (request.getMethod().equalsIgnoreCase("PATCH")){
//            doPatch(request, response);
//        } else {
//            super.service(request, response);
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        String path = request.getPathInfo();
        if (path == null || path.length() < 7) {
            responseGenerator.invalidCode();
            return;
        }
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        path = path.replaceFirst("/", "");
        String baseCode = path.substring(0,3).toUpperCase();
        String targetCode = path.substring(3,6).toUpperCase();

        try {
            Optional<ExchangeRate> optional = exchangeRateRepository.getRate(baseCode, targetCode);

            if (!optional.isPresent()) {
                responseGenerator.rateNotExists();
                return;
            }

            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            ExchangeRate exchangeRate = optional.get();

            out.print(gson.toJson(exchangeRate));
            out.flush();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Вместо PATCH
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            double rate = Double.parseDouble(request.getParameter("rate"));
            String path = request.getPathInfo();
            if (path == null || path.length() < 7) {
                responseGenerator.invalidCode();
                return;
            }
            path = path.replaceFirst("/", "");
            String baseCode = path.substring(0,3).toUpperCase();
            String targetCode = path.substring(3,6).toUpperCase();
            if (!currencyRepository.exists(baseCode) || !currencyRepository.exists(targetCode)) {
                responseGenerator.currencyNotExists();
                return;
            }
            if (!exchangeRateRepository.exists(baseCode, targetCode)) {
                responseGenerator.rateNotExists();
                return;
            }
            ExchangeRate exchangeRate = exchangeRateRepository.updateRate(baseCode, targetCode, rate);
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
