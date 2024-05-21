package jc.zeus.world.client;


import jc.zeus.world.client.departure.DepartureMessage;
import jc.zeus.world.client.enums.FAQueryParameter;
import jc.zeus.world.client.enums.ResponseCode;
import jc.zeus.world.client.enums.SchemaIDType;
import jc.zeus.world.client.query.QueryMessage;
import jc.zeus.world.client.query.SimpleFAQueryParameter;
import jc.zeus.world.client.utilities.Generator;
import jc.zeus.world.client.validation.ValidationMessage;
import org.apache.log4j.Logger;

import java.util.Map;

import static jc.zeus.world.client.utilities.Generator.formatNamed;
import static jc.zeus.world.client.utilities.Generator.formatXML;

public class MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(MessageGenerator.class);

    public static void main(String[] args) {
//        String message = generateDepartureMessage(true, "55260a7c-7c90-44fc-a12d-7c4f28735156");
//        String message = generateDepartureMessage(true);
//        String message = generateValidation(ResponseCode.OK, "55260a7c-7c90-44fc-a12d-7c4f28735156");
        String message = generateQUERY(false);


//        Generator generator =  new Generator();
//        for (int i = 0; i < 3; i++) {
//            String departureUUID = generator.generateUUID();
//            for (int j = 0; j < 1000; j++) {
//                String departure = generateDeparture(departureUUID);
//                SoapClient.sendMessage(departure);
//            }
//        }





        SoapClient.sendMessage(message);
    }

    private static String generateValidation(ResponseCode responseCode, String refUUID) {
        return new ValidationMessage.Builder(responseCode)
                .sendingCountryCode("POL")
                .receivingCountryCode("LTU")
                .refUuidString(refUUID)
                .creationDateTimeElement("2023-09-11", "07:45:00")
                .build()
                .generateValidationMessage();
    }

    private static String generateQUERY(boolean shouldFail) {
        if (shouldFail) {
            return new QueryMessage.Builder()
                    .simpleFAQueryParameterElement(new SimpleFAQueryParameter(FAQueryParameter.TRIPID_EU_TRIP_ID, "DNK-TRP-XXX"))
                    .build()
                    .generateQueryMessage();
        }
        return new QueryMessage.Builder()
                .sendingCountryCode("POL")
                .receivingCountryCode("LTU")
                .simpleFAQueryParameterElement(new SimpleFAQueryParameter(FAQueryParameter.TRIPID_EU_TRIP_ID, "DNK-TRP-75"))
                .specifiedDelimitedPeriodElement("2023-09-05", "10:10:00", "2023-10-30", "05:05:00")
                .build()
                .generateQueryMessage();
    }

    private static String generateDeparture(String departureUUID) {
        return new DepartureMessage.Builder()
                .departureUuidString(departureUUID)
                .build()
                .generateValidDepartureLog();
    }

    private static String generateDepartureMessageWithCatch(String departureUUID) {
        return new DepartureMessage.Builder()
                .departureUuidString(departureUUID)
                .build()
                .generateValidDepartureWithCatchLog();
    }

    private static String generateDepartureMessage(boolean isValid) {
        if (isValid) {
            return new DepartureMessage.Builder()
                    .build()
                    .generateValidDepartureLog();
        }
        return new DepartureMessage.Builder()
                .build()
                .generateDepartureLog();
    }

    private static String generateDepartureMessage(boolean isValid, String referenceID) {
        if (isValid) {
            return new DepartureMessage.Builder()
                    .referencedUuidString(SchemaIDType.UUID, referenceID)
                    .build()
                    .generateValidDepartureLog();
        }
        return new DepartureMessage.Builder()
                .referencedUuidString(SchemaIDType.UUID, referenceID)
                .build()
                .generateDepartureLog();
    }

    private static void stressTest() {
//        for (int i = 0; i < 100; i++) {

        String refID1 = "4a882ea4-b842-4eb7-a6dc-40caa6b2a241";
        SoapClient.sendMessage(generateDepartureMessage(true, refID1));
        SoapClient.sendMessage(generateValidation(ResponseCode.OK, refID1));

        String refID2 = "32619bd1-6601-4c9c-8e83-854cd16ee170";
        SoapClient.sendMessage(generateDepartureMessage(true, refID2));
        SoapClient.sendMessage(generateValidation(ResponseCode.OK, refID2));

        String refID3 = "7e86f31b-a054-4284-b101-9c949f653cb7";
        SoapClient.sendMessage(generateDepartureMessage(true, refID3));
        SoapClient.sendMessage(generateValidation(ResponseCode.OK, refID3));

//            String refID4 = "4dddcef5-dec2-4a07-a482-c73bc8e70d00";
//            SoapClient.sendMessage(generateDepartureMessage(true, refID4));
//            SoapClient.sendMessage(generateValidation(ResponseCode.OK, refID4));

//        }
    }

}