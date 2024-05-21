package jc.zeus.world.client.utilities;

import jc.zeus.world.client.enums.SchemaIDType;
import jc.zeus.world.client.query.SimpleFAQueryParameter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class Generator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final SimpleDateFormat isoFormat;
    private final Date now;

    public enum CalendarField {
        SECOND(Calendar.SECOND),
        MINUTE(Calendar.MINUTE),
        HOUR(Calendar.HOUR),
        DAY(Calendar.DAY_OF_MONTH),
        MONTH(Calendar.MONTH),
        YEAR(Calendar.YEAR);

        private final int value;

        CalendarField(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Generator() {
        this.isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.now = new Date();
    }

    public String generateOperationNumberFor(String countryCode) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String formattedDate = dateFormatter.format(now);
        return countryCode + formattedDate;
    }

    public String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String generateTODTString() {
        return isoFormat.format(now);
    }

    public String generateDateTime(CalendarField field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(field.value, amount);

        return isoFormat.format(cal.getTime());
    }


    public String generateSubmittedDateTimeElement(CalendarField field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(field.value, amount);
        String dateString = isoFormat.format(cal.getTime());

        return """
                <ram:SubmittedDateTime>
                    <ns2:DateTime>%s</ns2:DateTime>
                </ram:SubmittedDateTime>
                """.formatted(dateString);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        try {
            return LocalDateTime.parse(date + "T" + time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time format");
        }
    }

    public String generateReferencedIDElement(SchemaIDType schemaIDType, String value) {
        return String.format("""
                <ram:ReferencedID schemeID="%s">%s</ram:ReferencedID>
                """, schemaIDType, value);
    }

    public String generateCreationDateTimeElement(String date, String time) {
        try {
            LocalDateTime creation = parseDateTime(date, time);
            String creationDateTime = creation.format(OUTPUT_FORMATTER);
            return String.format("""
                    <CreationDateTime>
                      <ns2:DateTime>%s</ns2:DateTime>
                    </CreationDateTime>
                      """, creationDateTime);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String generateSpecifiedDelimitedPeriodElement(String startDate, String startTime, String endDate, String endTime) {
        try {
            LocalDateTime start = parseDateTime(startDate, startTime);
            LocalDateTime end = parseDateTime(endDate, endTime);

            String startDateTime = start.format(OUTPUT_FORMATTER);
            String endDateTime = end.format(OUTPUT_FORMATTER);

            return String.format("""
                    <ram:SpecifiedDelimitedPeriod>
                        <ram:StartDateTime>
                            <ns2:DateTime>%s</ns2:DateTime>
                        </ram:StartDateTime>
                        <ram:EndDateTime>
                            <ns2:DateTime>%s</ns2:DateTime>
                        </ram:EndDateTime>
                    </ram:SpecifiedDelimitedPeriod>
                    """, startDateTime, endDateTime);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }


    public static String generateOccurrenceDateTime(String occurrenceDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date date = inputFormat.parse(occurrenceDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, 4);
        cal.add(Calendar.MINUTE, 7);

        String occurrenceDateTime = outputFormat.format(cal.getTime());

        return """
                <OccurrenceDateTime>
                    <ns2:DateTime>%s</ns2:DateTime>
                </OccurrenceDateTime>
                """.formatted(occurrenceDateTime);
    }

    public static String formatXML(String unformattedXml) {
        try {
            unformattedXml = unformattedXml.trim().replaceAll(">[\\s]*<", "><");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            StreamSource source = new StreamSource(new StringReader(unformattedXml));
            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateFaQueryParameterElement(SimpleFAQueryParameter queryParameter) {
        String wsdlTemplate = """
                <ram:SimpleFAQueryParameter>
                    <ram:TypeCode listID="FA_QUERY_PARAMETER">{queryParam}</ram:TypeCode>
                    <ram:ValueID schemeID="{schemeID}">{identifier}</ram:ValueID>
                </ram:SimpleFAQueryParameter>
                """;

        Map<String, String> values = Map.of(
                "queryParam", queryParameter.getFaQueryParameter().getParameter(),
                "schemeID", queryParameter.getFaQueryParameter().getSchemeID(),
                "identifier", queryParameter.getCode()
        );

        return formatNamed(wsdlTemplate, values);
    }


    public static String formatNamed(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

}
