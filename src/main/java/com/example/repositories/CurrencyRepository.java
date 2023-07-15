package com.example.repositories;

import com.example.DataSourceProvider;
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
    private DataSourceProvider dataSourceProvider;
    private DataSource dataSource;

    public CurrencyRepository() throws URISyntaxException {
        dataSourceProvider = DataSourceProvider.getInstance();
        dataSource = dataSourceProvider.getDataSource();
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
        st.close();
        return list;
    }
    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
            Connection con = dataSource.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT * FROM currencies WHERE code = ?");
            st.setString(1, code);
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (!resultSet.next()) {
                st.close();
                return Optional.empty();
            }
            Optional<Currency> optional = Optional.of(new Currency(resultSet.getInt("id"), resultSet.getString("code"),
                    resultSet.getString("fullName"), resultSet.getString("sign")));
            st.close();
            return optional;

    }
    public boolean exists(String code) throws SQLException {
        return getCurrencyByCode(code).isPresent();
    }
    public Currency addCurrency(Currency currency) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("INSERT INTO currencies (code, fullName, sign) VALUES (?, ?, ?)");
        st.setString(1, currency.getCode());
        st.setString(2, currency.getFullName());
        st.setString(3, currency.getSign());
        st.executeUpdate();
        st.close();
        return getCurrencyByCode(currency.getCode()).get();
    }
}
