package com.example.utils;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DataSourceProvider {
    private static DataSourceProvider instance;
    private static DataSource dataSourceInstance;

    public static DataSourceProvider getInstance() {
        if (instance == null) {
            instance = new DataSourceProvider();
        }
        return instance;
    }
    public DataSourceProvider() {
    }
    public DataSource getDataSource() throws URISyntaxException {
        if (dataSourceInstance == null) {
            URL resource = DataSourceProvider.class.getClassLoader().getResource("currencyExchange.db");
            String path = null;
            if (resource != null) {
                path = new File(resource.toURI()).getAbsolutePath();
            }
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(String.format("jdbc:sqlite:%s", path));
            dataSourceInstance = dataSource;
        }
        return dataSourceInstance;
    }
}
