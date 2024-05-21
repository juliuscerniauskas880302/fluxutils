package jc.zeus.world.client.query;

import jc.zeus.world.client.enums.FAQueryParameter;
import jc.zeus.world.client.utilities.Generator;

import java.util.Map;

import static jc.zeus.world.client.utilities.Generator.formatNamed;
import static jc.zeus.world.client.utilities.Generator.formatXML;

public class QueryMessage {
    private final String sendingCountryCode;
    private final String receivingCountryCode;
    private final String operationNumber;
    private final String todtDateString;
    private final String uuidString;
    private final String submittedDateTimeElement;
    private final String specifiedDelimitedPeriodElement;
    private final String simpleFAQueryParameterElement;
    private final SimpleFAQueryParameter simpleFAQueryParameter;

    private QueryMessage(Builder builder) {
        Generator generator = builder.generator;
        this.operationNumber = generator.generateOperationNumberFor(builder.sendingCountryCode);
        this.uuidString = generator.generateUUID();
        this.todtDateString = generator.generateTODTString();
        this.submittedDateTimeElement = builder.submittedDateTimeElement;
        this.specifiedDelimitedPeriodElement = builder.specifiedDelimitedPeriodElement;
        this.sendingCountryCode = builder.sendingCountryCode;
        this.receivingCountryCode = builder.receivingCountryCode;
        this.simpleFAQueryParameterElement = builder.simpleFAQueryParameterElement;
        this.simpleFAQueryParameter = builder.simpleFAQueryParameter;
    }

    public String generateQueryMessage() {
        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2">
                            <ns3:FLUXFAQueryMessage xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXFAQueryMessage:3"
                            xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" 
                            xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" 
                            xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20">
                                <ns3:FAQuery>
                                    <ram:ID schemeID="UUID">{uuidString}</ram:ID>
                                    {submittedDateTimeElement}
                                    <ram:TypeCode listID="FA_QUERY_TYPE">{faQueryType}</ram:TypeCode>
                                    {specifiedDelimitedPeriodElement}
                                    <ram:SubmitterFLUXParty>
                                        <ram:ID schemeID="FLUX_GP_PARTY">POL</ram:ID>
                                    </ram:SubmitterFLUXParty>
                                    {simpleFAQueryParameterElement}
                                    <ram:SimpleFAQueryParameter>
                                        <ram:TypeCode listID="FA_QUERY_PARAMETER">CONSOLIDATED</ram:TypeCode>
                                        <ram:ValueCode listID="BOOLEAN_TYPE">Y</ram:ValueCode>
                                    </ram:SimpleFAQueryParameter>
                                </ns3:FAQuery>
                            </ns3:FLUXFAQueryMessage>
                        </urn:Connector2BridgeRequest>
                    </soapenv:Body>
                </soapenv:Envelope>
                """;

        Map<String, String> values = Map.of(
                "sendingCountryCode", sendingCountryCode,
                "receivingCountryCode", receivingCountryCode,
                "operationNumber", operationNumber,
                "todtDateString", todtDateString,
                "uuidString", uuidString,
                "submittedDateTimeElement", submittedDateTimeElement,
                "specifiedDelimitedPeriodElement", specifiedDelimitedPeriodElement,
                "simpleFAQueryParameterElement", simpleFAQueryParameterElement,
                "faQueryType", getFaQueryType(simpleFAQueryParameter)
        );

        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }

    private String getFaQueryType(SimpleFAQueryParameter simpleFAQueryParameter) {
        return simpleFAQueryParameter.getFaQueryParameter().getType();
    }

    public static class Builder {
        private final Generator generator;
        private String sendingCountryCode;
        private String receivingCountryCode;
        private String submittedDateTimeElement;
        private String specifiedDelimitedPeriodElement;
        private String simpleFAQueryParameterElement;
        private SimpleFAQueryParameter simpleFAQueryParameter;

        public Builder() {
            this.generator = new Generator();
            this.sendingCountryCode = "POL";
            this.receivingCountryCode = "LTU";
            this.submittedDateTimeElement = generator.generateSubmittedDateTimeElement(Generator.CalendarField.DAY, -5);
            this.specifiedDelimitedPeriodElement = generator.generateSpecifiedDelimitedPeriodElement("2023-09-05", "10:10:00", "2023-09-08", "05:05:00");
            this.simpleFAQueryParameter = new SimpleFAQueryParameter(FAQueryParameter.VESSELID_IRCS, "RD88");
            this.simpleFAQueryParameterElement = generator.generateFaQueryParameterElement(simpleFAQueryParameter);
        }

        public Builder sendingCountryCode(String sendingCountryCode) {
            this.sendingCountryCode = sendingCountryCode;
            return this;
        }

        public Builder receivingCountryCode(String receivingCountryCode) {
            this.receivingCountryCode = receivingCountryCode;
            return this;
        }

        public Builder submittedDateTimeElement(Generator.CalendarField field, int amount) {
            this.submittedDateTimeElement = generator.generateSubmittedDateTimeElement(field, amount);
            return this;
        }

        public Builder specifiedDelimitedPeriodElement(String startDate, String startTime, String endDate, String endTime) {
            this.specifiedDelimitedPeriodElement = generator.generateSpecifiedDelimitedPeriodElement(startDate, startTime, endDate, endTime);
            return this;
        }

        public Builder simpleFAQueryParameterElement(SimpleFAQueryParameter simpleFAQueryParameter) {
            this.simpleFAQueryParameter = simpleFAQueryParameter;
            this.simpleFAQueryParameterElement = generator.generateFaQueryParameterElement(simpleFAQueryParameter);
            return this;
        }

        public QueryMessage build() {
            return new QueryMessage(this);
        }
    }

}