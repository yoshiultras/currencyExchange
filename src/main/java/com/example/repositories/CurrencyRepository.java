package com.example.repositories;

import com.example.DataSourceFactory;
import com.example.models.Currency;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository {
    private DataSourceFactory dataSourceFactory;
    private DataSource dataSource;

    public CurrencyRepository() throws URISyntaxException {
        dataSourceFactory = DataSourceFactory.getInstance();
        dataSource = dataSourceFactory.getDataSource();
    }

    public List<Currency> getCurrencies() throws SQLException {
        List<Currency> list = new ArrayList<>();
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM currencies");
        st.execute();
        ResultSet resultSet = st.getResultSet();
        while (resultSet.next()) {
            list.add(new Currency(resultSet.getInt("id"), resultSet.getString("code"),
                    resultSet.getString("fullName"), resultSet.getString("sign")));
        }
        return list;
    }
    public boolean exists(String code) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM currencies WHERE code = ?");
        st.setString(1, code);
        st.execute();
        ResultSet resultSet = st.getResultSet();
        return resultSet.next();
    }
    public Currency addCurrency(Currency currency) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("INSERT INTO currencies (code, fullName, sign) VALUES (?, ?, ?)");
        st.setString(1, currency.getCode());
        st.setString(2, currency.getFullName());
        st.setString(3, currency.getSign());
        st.executeUpdate();
        return getLastCurrency();
    }
    public Currency getLastCurrency() throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM currencies ORDER BY id DESC LIMIT 1");
        st.execute();
        ResultSet resultSet = st.getResultSet();
        resultSet.next();
        return new Currency(resultSet.getInt("id"), resultSet.getString("code"),
                resultSet.getString("fullName"), resultSet.getString("sign"));
    }
}
