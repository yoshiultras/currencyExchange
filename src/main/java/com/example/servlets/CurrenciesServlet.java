package com.example.servlets;

import com.example.models.Currency;
import com.example.repositories.CurrencyRepository;
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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    CurrencyRepository currencyRepository;

    @Override
    public void init() {
        try {
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(request, response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        try {
            List<Currency> list = currencyRepository.getCurrencies();
            System.out.println(list.size());
            for (Currency currency : list) {
                out.print(gson.toJson(currency));
            }
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(request, response);
        request.setCharacterEncoding("UTF-8");
        String code = request.getParameter("code");
        String fullName = request.getParameter("fullName");
        String sign = request.getParameter("sign");
        if (code.length() != 3 || fullName.length() == 0 || sign.length() == 0) {
            responseGenerator.notValidCurrency();
            return;
        }
        try {
            if (currencyRepository.exists(code)) {
                responseGenerator.currencyExists();
                return;
            }
            Currency currency = new Currency(0, code, fullName, sign);
            Currency newCurrency = currencyRepository.addCurrency(currency);
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(newCurrency));
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }
}