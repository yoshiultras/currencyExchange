package com.example.repositories;

import com.example.DataSourceFactory;
import com.example.models.Currency;
import com.example.models.ExchangeRate;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateRepository {
    DataSourceFactory dataSourceFactory;
    DataSource dataSource;

    public ExchangeRateRepository() throws URISyntaxException {
        dataSourceFactory = DataSourceFactory.getInstance();
        dataSource = dataSourceFactory.getDataSource();
    }

    public List<ExchangeRate> getRates() throws SQLException {
        List<ExchangeRate> list = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT ex.id AS exid, b.id AS bid, b.code AS bcode, b.fullName AS bfullName, b.sign AS bsign, " +
                "t.id AS tid, t.code AS tcode, t.fullName AS tfullName, t.sign AS tsign, ex.rate AS exrate FROM exchangeRates AS ex " +
                "JOIN currencies AS b ON ex.baseCurrencyId = b.id " +
                "JOIN currencies AS t ON ex.targetCurrencyId = t.id;");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Currency base = new Currency(resultSet.getInt("bid"), resultSet.getString("bcode"),
                    resultSet.getString("bfullName"), resultSet.getString("bsign"));
            Currency target = new Currency(resultSet.getInt("tid"), resultSet.getString("tcode"),
                    resultSet.getString("tfullName"), resultSet.getString("tsign"));
            ExchangeRate exchangeRate = new ExchangeRate(resultSet.getInt("exid"), base, target, resultSet.getDouble("exrate"));
            list.add(exchangeRate);
        }
        return list;
    }
}
