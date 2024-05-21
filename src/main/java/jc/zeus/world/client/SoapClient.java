package jc.zeus.world.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SoapClient {

    public static void sendMessage(String xmlMessage) {
        try {
            // SOAP Endpoint URL
            URL url = new URL("http://localhost:8989/ws/v1-bridge-connector");

            // Create HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the header
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setDoOutput(true);

            // Send the request
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(xmlMessage.getBytes());
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Request sent successfully");
            } else {
                System.out.println("Failed to send the request. Response Code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
