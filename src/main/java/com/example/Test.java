package com.example;

import com.example.models.Currency;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws URISyntaxException, SQLException {
        DataSourceFactory dataSourceFactory = com.example.DataSourceFactory.getInstance();

        Connection con = dataSourceFactory.getDataSource().getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM currencies");
        st.execute();
        ResultSet resultSet = st.getResultSet();
        while (resultSet.next()) {
            System.out.println(new Currency(resultSet.getInt("id"), resultSet.getString("code"),
                    resultSet.getString("fullName"), resultSet.getString("sign")));
        }
    }
}
