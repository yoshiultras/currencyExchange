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
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            List<Currency> list = currencyRepository.getCurrencies();
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            for (Currency currency : list) {
                out.print(gson.toJson(currency));
            }
            out.flush();
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        request.setCharacterEncoding("UTF-8");

        try {
            String code = request.getParameter("code");
            String fullName = request.getParameter("fullName");
            String sign = request.getParameter("sign");
            if (fullName.length() == 0 || sign.length() == 0) {
                responseGenerator.notValidCurrency();
                return;
            }
            if(code.length() != 3) {
                responseGenerator.invalidCode();
                return;
            }
            if (currencyRepository.exists(code)) {
                responseGenerator.currencyExists();
                return;
            }
            Currency currency = new Currency(0, code, fullName, sign);
            Currency newCurrency = currencyRepository.addCurrency(currency);
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(newCurrency));
            out.flush();
        } catch (SQLException e) {
            responseGenerator.generalException();
        }
    }
}
