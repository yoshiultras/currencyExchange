package com.example.servlets;

import com.example.dto.Exchange;
import com.example.repositories.CurrencyRepository;
import com.example.services.ExchangeService;
import com.example.utils.ResponseGenerator;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ExchangeService exchangeService;
    CurrencyRepository currencyRepository;

    @Override
    public void init() {
        try {
            exchangeService = new ExchangeService();
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
            String fromCur = request.getParameter("from");
            String toCur = request.getParameter("to");
            double amount = Double.parseDouble(request.getParameter("amount"));
            if (fromCur == null || toCur == null) {
                responseGenerator.invalidCode();
                return;
            }
            if (fromCur.length() != 3 || toCur.length() != 3) {
                responseGenerator.invalidCode();
                return;
            }
            if (!currencyRepository.exists(fromCur) || !currencyRepository.exists(toCur)) {
                responseGenerator.currencyNotExists();
                return;
            }
            Exchange exchange = exchangeService.exchange(fromCur, toCur, amount);
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(exchange));
            out.flush();
        } catch (SQLException e) {
            System.out.println(e);
            responseGenerator.generalException();
        } catch (Exception e) {
            responseGenerator.invalidAmount();
        }

    }

}
