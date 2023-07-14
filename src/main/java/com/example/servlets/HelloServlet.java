package com.example.servlets;

import com.example.DataSourceFactory;
import com.example.models.Currency;
import com.google.gson.Gson;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/h")
public class HelloServlet extends HttpServlet {
    private String message;

    @Override
    public void init() {
        message = "Hello World!";
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        DataSourceFactory dataSourceFactory = com.example.DataSourceFactory.getInstance();
        try {
            List<Currency> list = new ArrayList<>();
            Connection con = dataSourceFactory.getDataSource().getConnection();
            PreparedStatement st = con.prepareStatement("SELECT * FROM currencies");
            st.execute();
            ResultSet resultSet = st.getResultSet();
            while (resultSet.next()) {
                list.add(new Currency(resultSet.getInt("id"), resultSet.getString("code"),
                        resultSet.getString("fullName"), resultSet.getString("sign")));
            }
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            for (Currency currency : list) {
                out.print(gson.toJson(currency));
            }

            out.flush();
        } catch (Exception e) {

        }
    }

    @Override
    public void destroy() {
    }
}