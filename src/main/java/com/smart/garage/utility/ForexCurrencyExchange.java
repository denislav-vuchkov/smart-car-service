package com.smart.garage.utility;

import com.smart.garage.models.Currencies;
import org.cloudinary.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ForexCurrencyExchange {

    public Double convertPriceFromBGNToForeignCurrency(Currencies currency, int priceInBGN) throws IOException {
        //TODO Our trial for FOREX exchange rates expires on 26 April
        //I am doing the conversion from this website --> https://console.fastforex.io/
        String API_KEY_FOREX = "4ad14a96fc-05d65be781-rakr1p";

        String url = "https://api.fastforex.io/fetch-one?from=BGN&to=" + currency.toString() + "&api_key=" + API_KEY_FOREX;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("Accept", "application/json");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());

        double exchangeRate = Double.parseDouble(jsonObject.getJSONObject("result").get(currency.toString()).toString());

        return priceInBGN * exchangeRate;
    }

}
