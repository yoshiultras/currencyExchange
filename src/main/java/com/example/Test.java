package com.example;

import com.example.models.Currency;
import com.example.repositories.CurrencyRepository;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;

public class Test {
    public static void main(String[] args) throws URISyntaxException, SQLException {
        DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();
        DataSource dataSource = dataSourceFactory.getDataSource();
        Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("DELETE FROM currencies WHERE code = 'CAD'");
        st.execute();
        CurrencyRepository rep = new CurrencyRepository();
        rep.addCurrency(new Currency(0, "CAD", "CANAD", "A$"));
        List<Currency> list = rep.getCurrencies();
        for (Currency currency : list) {
            System.out.println(currency);
        }
    }
}
