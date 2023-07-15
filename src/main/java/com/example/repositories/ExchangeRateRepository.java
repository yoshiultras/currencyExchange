package com.example.repositories;

import com.example.DataSourceProvider;
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
import java.util.Optional;

public class ExchangeRateRepository {
    DataSourceProvider dataSourceProvider;
    DataSource dataSource;

    public ExchangeRateRepository() throws URISyntaxException {
        dataSourceProvider = DataSourceProvider.getInstance();
        dataSource = dataSourceProvider.getDataSource();
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
        statement.close();
        return list;
    }
    public Optional<ExchangeRate> getRate(String baseCode, String targetCode) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT ex.id AS exid, b.id AS bid, b.code AS bcode, b.fullName AS bfullName, b.sign AS bsign, " +
                "t.id AS tid, t.code AS tcode, t.fullName AS tfullName, t.sign AS tsign, ex.rate AS exrate FROM exchangeRates AS ex " +
                "JOIN currencies AS b ON ex.baseCurrencyId = b.id " +
                "JOIN currencies AS t ON ex.targetCurrencyId = t.id WHERE bcode = ? AND tcode = ?;");

        statement.setString(1, baseCode);
        statement.setString(2, targetCode);
        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) return Optional.empty();

        Currency base = new Currency(resultSet.getInt("bid"), resultSet.getString("bcode"),
                resultSet.getString("bfullName"), resultSet.getString("bsign"));

        Currency target = new Currency(resultSet.getInt("tid"), resultSet.getString("tcode"),
                resultSet.getString("tfullName"), resultSet.getString("tsign"));
        Optional<ExchangeRate> optional = Optional.of(new ExchangeRate(resultSet.getInt("exid"), base, target, resultSet.getDouble("exrate")));
        statement.close();
        return optional;
    }

    public boolean exists(String baseCode, String targetCode) throws SQLException {
        return getRate(baseCode, targetCode).isPresent();
    }

    public ExchangeRate addRate(String baseCode, String targetCode, double rate) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement statement = connection.prepareStatement("INSERT INTO exchangeRates (baseCurrencyId, targetCurrencyId, rate) " +
                "VALUES (" + getCurrencyId(baseCode) + ", " + getCurrencyId(targetCode) + ", ?");

        statement.setDouble(1, rate);

        statement.executeUpdate();
        statement.close();
        return getRate(baseCode, targetCode).get();
    }
    public ExchangeRate updateRate(String baseCode, String targetCode, double rate) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement statement = connection.prepareStatement("UPDATE exchangeRates SET rate = ? " +
                "WHERE baseCurrencyId = " + getCurrencyId(baseCode) +" AND targetCurrencyId = " + getCurrencyId(targetCode) + ";");

        statement.setDouble(1, rate);

        statement.executeUpdate();
        statement.close();
        return getRate(baseCode, targetCode).get();
    }
    private int getCurrencyId(String code) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT id FROM currencies WHERE code = ?");
        statement.setString(1, code);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int id = resultSet.getInt("id");
        statement.close();
        return id;
    }

}
