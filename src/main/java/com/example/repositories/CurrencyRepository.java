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
}
