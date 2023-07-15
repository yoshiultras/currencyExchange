package com.example;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DataSourceFactory {
    private static DataSourceFactory instance;
    private static DataSource dataSourceInstance;

    public static DataSourceFactory getInstance() {
        if (instance == null) {
            instance = new DataSourceFactory();
        }
        return instance;
    }
    public DataSourceFactory() {
    }
    public DataSource getDataSource() throws URISyntaxException {
        if (dataSourceInstance == null) {
            URL resource = DataSourceFactory.class.getClassLoader().getResource("currencyExchange.db");
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
