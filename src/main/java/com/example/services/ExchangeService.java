package com.example.services;

import com.example.dto.Exchange;
import com.example.models.ExchangeRate;
import com.example.repositories.CurrencyRepository;
import com.example.repositories.ExchangeRateRepository;

import java.net.URISyntaxException;
import java.sql.SQLException;

public class ExchangeService {
    ExchangeRateRepository exchangeRateRepository;
    CurrencyRepository currencyRepository;

    public ExchangeService() throws URISyntaxException {
        exchangeRateRepository = new ExchangeRateRepository();
        currencyRepository = new CurrencyRepository();
    }
    public Exchange exchange(String baseCode, String targetCode, double amount) throws SQLException {

        if (exchangeRateRepository.exists(baseCode, targetCode)) {
            ExchangeRate exchangeRate = exchangeRateRepository.getRate(baseCode, targetCode).get();
            double rate = exchangeRate.getRate();
            return new Exchange(currencyRepository.getCurrencyByCode(baseCode).get(), currencyRepository.getCurrencyByCode(targetCode).get(),
                    rate, amount, amount * rate);
        }

        if (exchangeRateRepository.exists(targetCode, baseCode)) {
            ExchangeRate exchangeRate = exchangeRateRepository.getRate(targetCode, baseCode).get();
            double rate = exchangeRate.getRate();
            return new Exchange(currencyRepository.getCurrencyByCode(targetCode).get(), currencyRepository.getCurrencyByCode(baseCode).get(),
                    1 / rate, amount, amount / rate);
        }

        ExchangeRate exchangeRateTo = exchangeRateRepository.getRate( "USD", baseCode).get();
        ExchangeRate exchangeRateFrom = exchangeRateRepository.getRate("USD", targetCode).get();
        double rate = 1 / exchangeRateTo.getRate() * exchangeRateFrom.getRate();
        return new Exchange(currencyRepository.getCurrencyByCode(targetCode).get(), currencyRepository.getCurrencyByCode(baseCode).get(),
                rate, amount, amount * rate);
    }
}
