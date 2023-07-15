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
import java.util.Optional;

@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {
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

        String path = request.getPathInfo();
        if (path == null || path.length() < 4) {
            responseGenerator.currencyNotExists();
            return;
        }

        String code = path.replaceFirst("/", "").toUpperCase();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            Optional<Currency> optional = currencyRepository.getCurrencyByCode(code);
            if (!optional.isPresent()) {
                responseGenerator.currencyNotExists();
                return;
            }
            Currency currency = optional.get();
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(currency));
            out.flush();
        } catch (Exception e) {
            responseGenerator.generalException();
        }
    }
}
