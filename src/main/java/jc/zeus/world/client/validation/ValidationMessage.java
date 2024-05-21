package jc.zeus.world.client.validation;

import jc.zeus.world.client.enums.ResponseCode;
import jc.zeus.world.client.utilities.Generator;

import java.util.Map;

import static jc.zeus.world.client.utilities.Generator.formatNamed;
import static jc.zeus.world.client.utilities.Generator.formatXML;

public class ValidationMessage {
    private final String sendingCountryCode;
    private final String receivingCountryCode;
    private final String operationNumber;
    private final String todtDateString;
    private final String uuidString;
    private final String refUuidString;
    private final String creationDateTimeElement;
    private final ResponseCode responseCode;

    private ValidationMessage(Builder builder) {
        Generator generator = builder.generator;
        this.operationNumber = generator.generateOperationNumberFor(builder.sendingCountryCode);
        this.uuidString = generator.generateUUID();
        this.refUuidString = builder.refUuidString;
        this.todtDateString = generator.generateTODTString();
        this.creationDateTimeElement = builder.creationDateTimeElement;
        this.sendingCountryCode = builder.sendingCountryCode;
        this.receivingCountryCode = builder.receivingCountryCode;
        this.responseCode = builder.responseCode;
    }

    public String generateValidationMessage() {
        if (ResponseCode.OK.equals(responseCode)) {
            return generateValidMessage();
        } else {
            return generateInvalidMessage();
        }
    }

    public static class Builder {
        private final Generator generator;
        private final ResponseCode responseCode;
        private String sendingCountryCode;
        private String receivingCountryCode;
        private String creationDateTimeElement;
        private String refUuidString;

        public Builder(ResponseCode responseCode) {
            this.generator = new Generator();
            this.responseCode = responseCode;
            this.sendingCountryCode = "POL";
            this.receivingCountryCode = "LTU";
            this.refUuidString = "CHANGE REF UUID VALUE!!!";
            this.creationDateTimeElement = generator.generateCreationDateTimeElement("2023-09-05", "10:10:00");
        }

        public Builder sendingCountryCode(String sendingCountryCode) {
            this.sendingCountryCode = sendingCountryCode;
            return this;
        }

        public Builder receivingCountryCode(String receivingCountryCode) {
            this.receivingCountryCode = receivingCountryCode;
            return this;
        }

        public Builder refUuidString(String refUuidString) {
            this.refUuidString = refUuidString;
            return this;
        }

        public Builder creationDateTimeElement(String date, String time) {
            this.creationDateTimeElement = generator.generateCreationDateTimeElement(date, time);
            return this;
        }

        public ValidationMessage build() {
            return new ValidationMessage(this);
        }
    }

    private String generateInvalidMessage() {
        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}:FTF" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2">
                            <ns3:FLUXResponseMessage xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXResponseMessage:6">
                               <ns3:FLUXResponseDocument>
                                  <ID schemeID="UUID">{uuidString}</ID>
                                  <ReferencedID schemeID="UUID">{refUuidString}</ReferencedID>
                                  {creationDateTimeElement}
                                 <ResponseCode listID="FLUX_GP_RESPONSE">NOK</ResponseCode>
                                  <RelatedValidationResultDocument>
                                     <ValidatorID schemeID="FLUX_GP_PARTY">{receivingCountryCode}</ValidatorID>
                                     {creationDateTimeElement}
                                     <RelatedValidationQualityAnalysis>
                                        <LevelCode listID="FLUX_GP_VALIDATION_LEVEL">L02</LevelCode>
                                        <TypeCode listID="FLUX_GP_VALIDATION_TYPE">ERR</TypeCode>
                                        <Result languageID="GBR">ApplicableFLUXCharacteristic must be present if more than one occurrence of SpecifiedFLUXLocation/ID is present with the same attribute schemeID.</Result>
                                        <ID schemeID="FA_BR">FA-L02-00-0665</ID>
                                        <ReferencedItem languageID="XPATH">/FLUXFAReportMessage/FAReportDocument[1]/ram:SpecifiedFishingActivity[1]/SpecifiedFACatch[1]/SpecifiedFLUXLocation/ApplicableFLUXCharacteristic</ReferencedItem>
                                     </RelatedValidationQualityAnalysis>
                                     <RelatedValidationQualityAnalysis>
                                        <LevelCode listID="FLUX_GP_VALIDATION_LEVEL">L02</LevelCode>
                                        <TypeCode listID="FLUX_GP_VALIDATION_TYPE">ERR</TypeCode>
                                        <Result languageID="GBR">ApplicableFLUXCharacteristic/TypeCode must be MAIN_AREA if more than one occurrence of SpecifiedFLUXLocation/ID is present with the same attribute schemeID.</Result>
                                        <ID schemeID="FA_BR">FA-L02-00-0666</ID>
                                        <ReferencedItem languageID="XPATH">/FLUXFAReportMessage/FAReportDocument[1]/ram:SpecifiedFishingActivity[1]/SpecifiedFLUXLocation[1]/ApplicableFLUXCharacteristic</ReferencedItem>
                                     </RelatedValidationQualityAnalysis>
                                     <RelatedValidationQualityAnalysis>
                                        <LevelCode listID="FLUX_GP_VALIDATION_LEVEL">L03</LevelCode>
                                        <TypeCode listID="FLUX_GP_VALIDATION_TYPE">WAR</TypeCode>
                                        <Result languageID="GBR">The vessel should be in the EU fleet register under the flag state at the report creation date</Result>
                                        <ID schemeID="FA_BR">FA-L03-00-0064</ID>
                                        <ReferencedItem languageID="XPATH">/FLUXFAReportMessage/FAReportDocument[1]/ram:SpecifiedVesselTransportMeans/ram:ID</ReferencedItem>
                                     </RelatedValidationQualityAnalysis>
                                  </RelatedValidationResultDocument>
                                  <RespondentFLUXParty>
                                     <ID schemeID="FLUX_GP_PARTY">LTU</ID>
                                  </RespondentFLUXParty>
                               </ns3:FLUXResponseDocument>
                            </ns3:FLUXResponseMessage>
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
                "creationDateTimeElement", creationDateTimeElement,
                "refUuidString", refUuidString
        );

        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }

    private String generateValidMessage() {
        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2" xmlns:ns2="urn:xeu:connector-bridge:v1">
                            <ns3:FLUXResponseMessage xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXResponseMessage:6">
                                <ns3:FLUXResponseDocument>
                                    <ID schemeID="UUID">{uuidString}</ID>
                                    <ReferencedID schemeID="UUID">{refUuidString}</ReferencedID>
                                    {creationDateTimeElement}
                                    <ResponseCode listID="FLUX_GP_RESPONSE">OK</ResponseCode>
                                    <RespondentFLUXParty>
                                        <ID schemeID="FLUX_GP_PARTY">{receivingCountryCode}</ID>
                                    </RespondentFLUXParty>
                                </ns3:FLUXResponseDocument>
                            </ns3:FLUXResponseMessage>
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
                "creationDateTimeElement", creationDateTimeElement,
                "refUuidString", refUuidString
        );

        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }
}
