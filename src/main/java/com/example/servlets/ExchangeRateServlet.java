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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {
    ExchangeRateRepository exchangeRateRepository;

    @Override
    public void init() {
        try {
            exchangeRateRepository = new ExchangeRateRepository();
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

    //Вместо PATCH
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            Map<String, String[]> map = request.getParameterMap();
            System.out.println(Arrays.toString(map.get("rate")));
            double rate = Double.parseDouble(request.getParameter("rate"));
            String path = request.getPathInfo();
            System.out.println(path);
            if (path == null || path.length() < 7) {
                responseGenerator.rateNotExists();
                return;
            }
            path = path.replaceFirst("/", "");
            String baseCode = path.substring(0,3).toUpperCase();
            String targetCode = path.substring(3,6).toUpperCase();

            ExchangeRate exchangeRate = exchangeRateRepository.updateRate(baseCode, targetCode, rate);
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(exchangeRate));
            out.flush();

        } catch (ClassCastException | NullPointerException e) {
            responseGenerator.notValidRate();
        } catch (Exception e) {
            System.out.println(e);
            responseGenerator.generalException();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);

        String path = request.getPathInfo();
        if (path == null || path.length() < 7) {
            responseGenerator.rateNotExists();
            return;
        }
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

}
