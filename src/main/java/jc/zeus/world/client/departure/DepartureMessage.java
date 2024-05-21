package jc.zeus.world.client.departure;

import jc.zeus.world.client.enums.SchemaIDType;
import jc.zeus.world.client.utilities.Generator;

import java.util.Map;

import static jc.zeus.world.client.utilities.Generator.formatNamed;
import static jc.zeus.world.client.utilities.Generator.formatXML;

public class DepartureMessage {
    private final String sendingCountryCode;
    private final String receivingCountryCode;
    private final String operationNumber;
    private final String todtDateString;
    private final String documentUuidString;
    private final String departureUuidString;
    private final String creationDateTimeElement;
    private final String referencedUuidString;

    private DepartureMessage(Builder builder) {
        Generator generator = builder.generator;
        this.operationNumber = generator.generateOperationNumberFor(builder.sendingCountryCode);
        this.documentUuidString = builder.documentUuidString;
        this.departureUuidString = builder.departureUuidString;
        this.todtDateString = generator.generateTODTString();
        this.sendingCountryCode = builder.sendingCountryCode;
        this.receivingCountryCode = builder.receivingCountryCode;
        this.creationDateTimeElement = builder.creationDateTimeElement;
        this.referencedUuidString = builder.referencedUuidString;
    }

    public static class Builder {
        private final Generator generator;
        private String sendingCountryCode;
        private String receivingCountryCode;
        private String creationDateTimeElement;
        private String referencedUuidString;
        private String documentUuidString;
        private String departureUuidString;

        public Builder() {
            this.generator = new Generator();
            this.sendingCountryCode = "POL";
            this.receivingCountryCode = "LTU";
            this.referencedUuidString = "";
            this.creationDateTimeElement = generator.generateCreationDateTimeElement("2023-09-05", "10:10:00");
            this.documentUuidString = generator.generateUUID();
            this.departureUuidString = generator.generateUUID();
        }

        public Builder sendingCountryCode(String sendingCountryCode) {
            this.sendingCountryCode = sendingCountryCode;
            return this;
        }

        public Builder receivingCountryCode(String receivingCountryCode) {
            this.receivingCountryCode = receivingCountryCode;
            return this;
        }

        public Builder creationDateTimeElement(String date, String time) {
            this.creationDateTimeElement = generator.generateCreationDateTimeElement(date, time);
            return this;
        }

        public Builder documentUuidString(String documentUuidString) {
            this.documentUuidString = documentUuidString;
            return this;
        }

        public Builder departureUuidString(String departureUuidString) {
            this.departureUuidString = departureUuidString;
            return this;
        }

        public Builder referencedUuidString(SchemaIDType schemaIDType, String value) {
            this.referencedUuidString = generator.generateReferencedIDElement(schemaIDType, value);
            return this;
        }

        public DepartureMessage build() {
            return new DepartureMessage(this);
        }

    }

    public String generateValidDepartureLog() {
        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2">
                         <ns3:FLUXFAReportMessage 
                         xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" 
                         xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" 
                         xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXFAReportMessage:3"
                         xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20">
                            <ns3:FLUXReportDocument>
                               <ID schemeID="UUID">{documentUuidString}</ID>
                               {referencedUuidString}
                              {creationDateTimeElement}
                               <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                               <OwnerFLUXParty>
                                  <ID schemeID="FLUX_GP_PARTY">{sendingCountryCode}</ID>
                               </OwnerFLUXParty>
                            </ns3:FLUXReportDocument>
                            <ns3:FAReportDocument>
                               <TypeCode listID="FLUX_FA_REPORT_TYPE">DECLARATION</TypeCode>
                               <AcceptanceDateTime>
                                  <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                               </AcceptanceDateTime>
                               <RelatedFLUXReportDocument>
                                  <ID schemeID="UUID">{departureUuidString}</ID>
                                  <CreationDateTime>
                                     <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                                  </CreationDateTime>
                                  <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                                  <OwnerFLUXParty>
                                     <ID schemeID="FLUX_GP_PARTY">{sendingCountryCode}</ID>
                                  </OwnerFLUXParty>
                               </RelatedFLUXReportDocument>
                               <SpecifiedFishingActivity>
                                  <TypeCode listID="FLUX_FA_TYPE">DEPARTURE</TypeCode>
                                  <OccurrenceDateTime>
                                     <ns2:DateTime>2022-09-15T04:07:00Z</ns2:DateTime>
                                  </OccurrenceDateTime>
                                  <ReasonCode listID="FA_REASON_DEPARTURE">FIS</ReasonCode>
                                  <RelatedFLUXLocation>
                                     <TypeCode listID="FLUX_LOCATION_TYPE">LOCATION</TypeCode>
                                     <CountryID schemeID="TERRITORY">{sendingCountryCode}</CountryID>
                                     <ID schemeID="LOCATION">LTKLJ</ID>
                                  </RelatedFLUXLocation>
                                  <SpecifiedFishingTrip>
                                     <ID schemeID="EU_TRIP_ID">LTU-TRP-101458</ID>
                                  </SpecifiedFishingTrip>
                               </SpecifiedFishingActivity>
                               <SpecifiedVesselTransportMeans>
                                  <ID schemeID="CFR">DNK900001008</ID>
                                  <ID schemeID="IRCS">RD88</ID>
                                  <ID schemeID="EXT_MARK">X108</ID>
                                  <RegistrationVesselCountry>
                                     <ID schemeID="TERRITORY">DNK</ID>
                                  </RegistrationVesselCountry>
                                  <SpecifiedContactParty>
                                     <RoleCode listID="FLUX_CONTACT_ROLE">MASTER</RoleCode>
                                     <SpecifiedStructuredAddress>
                                        <StreetName>Unknown</StreetName>
                                        <CityName>Unknown</CityName>
                                        <CountryID schemeID="TERRITORY">SVN</CountryID>
                                        <PlotIdentification>Unknown</PlotIdentification>
                                        <PostalArea>Unknown</PostalArea>
                                     </SpecifiedStructuredAddress>
                                     <SpecifiedContactPerson>
                                        <GivenName>Captain Test</GivenName>
                                        <FamilyName>Captain Test</FamilyName>
                                     </SpecifiedContactPerson>
                                  </SpecifiedContactParty>
                               </SpecifiedVesselTransportMeans>
                            </ns3:FAReportDocument>
                         </ns3:FLUXFAReportMessage>
                      </urn:Connector2BridgeRequest>
                   </soapenv:Body>
                </soapenv:Envelope>
                """;

        Map<String, String> values = Map.of(
                "sendingCountryCode", sendingCountryCode,
                "receivingCountryCode", receivingCountryCode,
                "operationNumber", operationNumber,
                "todtDateString", todtDateString,
                "documentUuidString", documentUuidString,
                "departureUuidString", departureUuidString,
                "creationDateTimeElement", creationDateTimeElement,
                "referencedUuidString", referencedUuidString
        );

        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }

    public String generateDepartureLog() {
        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2">
                         <ns3:FLUXFAReportMessage 
                         xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" 
                         xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" 
                         xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXFAReportMessage:3"
                         xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20">
                            <ns3:FLUXReportDocument>
                               <ID schemeID="UUID">{documentUuidString}</ID>
                               {referencedUuidString}
                              {creationDateTimeElement}
                               <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                               <OwnerFLUXParty>
                                  <ID schemeID="FLUX_GP_PARTY">{receivingCountryCode}</ID>
                               </OwnerFLUXParty>
                            </ns3:FLUXReportDocument>
                            <ns3:FAReportDocument>
                               <TypeCode listID="FLUX_FA_REPORT_TYPE">DECLARATION</TypeCode>
                               <AcceptanceDateTime>
                                  <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                               </AcceptanceDateTime>
                               <RelatedFLUXReportDocument>
                                  <ID schemeID="UUID">{departureUuidString}</ID>
                                  <CreationDateTime>
                                     <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                                  </CreationDateTime>
                                  <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                                  <OwnerFLUXParty>
                                     <ID schemeID="FLUX_GP_PARTY">POL</ID>
                                  </OwnerFLUXParty>
                               </RelatedFLUXReportDocument>
                               <SpecifiedFishingActivity>
                                  <TypeCode listID="FLUX_FA_TYPE">DEPARTURE</TypeCode>
                                  <OccurrenceDateTime>
                                     <ns2:DateTime>2022-09-15T04:07:00Z</ns2:DateTime>
                                  </OccurrenceDateTime>
                                  <ReasonCode listID="FA_REASON_DEPARTURE">FIS</ReasonCode>
                                  <RelatedFLUXLocation>
                                     <TypeCode listID="FLUX_LOCATION_TYPE">LOCATION</TypeCode>
                                     <CountryID schemeID="TERRITORY">LTU</CountryID>
                                     <ID schemeID="LOCATION">LTKLJ</ID>
                                  </RelatedFLUXLocation>
                                  <SpecifiedFishingGear>
                                     <TypeCode listID="GEAR_TYPE">LHP</TypeCode>
                                     <RoleCode listID="FA_GEAR_ROLE">ONBOARD</RoleCode>
                                     <ApplicableGearCharacteristic>
                                        <TypeCode listID="FA_GEAR_CHARACTERISTIC">GN</TypeCode>
                                        <ValueQuantity unitCode="C62">20</ValueQuantity>
                                     </ApplicableGearCharacteristic>
                                     <ApplicableGearCharacteristic>
                                        <TypeCode listID="FA_GEAR_CHARACTERISTIC">NI</TypeCode>
                                        <ValueQuantity unitCode="C62">20</ValueQuantity>
                                     </ApplicableGearCharacteristic>
                                  </SpecifiedFishingGear>
                                  <SpecifiedFishingTrip>
                                     <ID schemeID="EU_TRIP_ID">LTU-TRP-101457</ID>
                                  </SpecifiedFishingTrip>
                               </SpecifiedFishingActivity>
                               <SpecifiedVesselTransportMeans>
                                  <ID schemeID="CFR">SVN123456789</ID>
                                  <ID schemeID="IRCS">IRCS4</ID>
                                  <ID schemeID="EXT_MARK">XR004</ID>
                                  <RegistrationVesselCountry>
                                     <ID schemeID="TERRITORY">SVN</ID>
                                  </RegistrationVesselCountry>
                                  <SpecifiedContactParty>
                                     <RoleCode listID="FLUX_CONTACT_ROLE">MASTER</RoleCode>
                                     <SpecifiedStructuredAddress>
                                        <StreetName>Unknown</StreetName>
                                        <CityName>Unknown</CityName>
                                        <CountryID schemeID="TERRITORY">SVN</CountryID>
                                        <PlotIdentification>Unknown</PlotIdentification>
                                        <PostalArea>Unknown</PostalArea>
                                     </SpecifiedStructuredAddress>
                                     <SpecifiedContactPerson>
                                        <GivenName>Captain Test</GivenName>
                                        <FamilyName>Captain Test</FamilyName>
                                     </SpecifiedContactPerson>
                                  </SpecifiedContactParty>
                               </SpecifiedVesselTransportMeans>
                            </ns3:FAReportDocument>
                         </ns3:FLUXFAReportMessage>
                      </urn:Connector2BridgeRequest>
                   </soapenv:Body>
                </soapenv:Envelope>
                """;

        Map<String, String> values = Map.of(
                "sendingCountryCode", sendingCountryCode,
                "receivingCountryCode", receivingCountryCode,
                "operationNumber", operationNumber,
                "todtDateString", todtDateString,
                "documentUuidString", documentUuidString,
                "departureUuidString", departureUuidString,
                "creationDateTimeElement", creationDateTimeElement,
                "referencedUuidString", referencedUuidString
        );

        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }


    public String generateValidDepartureWithCatchLog() {
        String catchUuidString = new Generator().generateUUID();
        StringBuilder catchPart = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            Map<String, String> val = Map.of(
                    "sendingCountryCode", sendingCountryCode,
                    "receivingCountryCode", receivingCountryCode,
                    "operationNumber", operationNumber,
                    "todtDateString", todtDateString,
                    "documentUuidString", documentUuidString,
                    "departureUuidString", departureUuidString,
                    "catchUuidString", new Generator().generateUUID(),
                    "creationDateTimeElement", creationDateTimeElement,
                    "referencedUuidString", referencedUuidString);

            String catchWSDL = """
                <ns3:FAReportDocument>
                                    <TypeCode listID="FLUX_FA_REPORT_TYPE">DECLARATION</TypeCode>
                                    <AcceptanceDateTime>
                                        <ns2:DateTime>2024-03-25T07:57:07Z</ns2:DateTime>
                                    </AcceptanceDateTime>
                                    <RelatedFLUXReportDocument>
                                        <ID schemeID="UUID">{catchUuidString}</ID>
                                        <CreationDateTime>
                                            <ns2:DateTime>2024-03-25T07:57:07Z</ns2:DateTime>
                                        </CreationDateTime>
                                        <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                                        <OwnerFLUXParty>
                                            <ID schemeID="FLUX_GP_PARTY">{sendingCountryCode}</ID>
                                        </OwnerFLUXParty>
                                    </RelatedFLUXReportDocument>
                                    <SpecifiedFishingActivity>
                                        <TypeCode listID="FLUX_FA_TYPE">FISHING_OPERATION</TypeCode>
                                        <OccurrenceDateTime>
                                            <ns2:DateTime>2024-03-25T07:47:00Z</ns2:DateTime>
                                        </OccurrenceDateTime>
                                        <VesselRelatedActivityCode listID="VESSEL_ACTIVITY">FIS</VesselRelatedActivityCode>
                                        <OperationsQuantity unitCode="C62">1</OperationsQuantity>
                                        <RelatedFLUXLocation>
                                            <TypeCode listID="FLUX_LOCATION_TYPE">AREA</TypeCode>
                                            <ID schemeID="TERRITORY">POL</ID>
                                        </RelatedFLUXLocation>
                                        <RelatedFLUXLocation>
                                            <TypeCode listID="FLUX_LOCATION_TYPE">AREA</TypeCode>
                                            <ID schemeID="FAO_AREA">71.6.1</ID>
                                        </RelatedFLUXLocation>
                                        <RelatedFLUXLocation>
                                            <TypeCode listID="FLUX_LOCATION_TYPE">AREA</TypeCode>
                                            <ID schemeID="MANAGEMENT_AREA">NAFO_RA</ID>
                                        </RelatedFLUXLocation>
                                        <RelatedFLUXLocation>
                                            <TypeCode listID="FLUX_LOCATION_TYPE">POSITION</TypeCode>
                                            <SpecifiedPhysicalFLUXGeographicalCoordinate>
                                                <LongitudeMeasure>-137.077580</LongitudeMeasure>
                                                <LatitudeMeasure>-85.059978</LatitudeMeasure>
                                            </SpecifiedPhysicalFLUXGeographicalCoordinate>
                                        </RelatedFLUXLocation>
                                        <RelatedFLUXLocation>
                                            <TypeCode listID="FLUX_LOCATION_TYPE">POSITION</TypeCode>
                                            <SpecifiedPhysicalFLUXGeographicalCoordinate>
                                                <LongitudeMeasure>-104.059772</LongitudeMeasure>
                                                <LatitudeMeasure>-2.807613</LatitudeMeasure>
                                            </SpecifiedPhysicalFLUXGeographicalCoordinate>
                                        </RelatedFLUXLocation>
                                        <SpecifiedFishingTrip>
                                            <ID schemeID="EU_TRIP_ID">LTU-TRP-101458</ID>
                                        </SpecifiedFishingTrip>
                                    </SpecifiedFishingActivity>
                                    <SpecifiedVesselTransportMeans>
                                      <ID schemeID="CFR">DNK900001008</ID>
                                      <ID schemeID="IRCS">RD88</ID>
                                      <ID schemeID="EXT_MARK">X108</ID>
                                      <RegistrationVesselCountry>
                                         <ID schemeID="TERRITORY">DNK</ID>
                                      </RegistrationVesselCountry>
                                      <SpecifiedContactParty>
                                         <RoleCode listID="FLUX_CONTACT_ROLE">MASTER</RoleCode>
                                         <SpecifiedStructuredAddress>
                                            <StreetName>Unknown</StreetName>
                                            <CityName>Unknown</CityName>
                                            <CountryID schemeID="TERRITORY">SVN</CountryID>
                                            <PlotIdentification>Unknown</PlotIdentification>
                                            <PostalArea>Unknown</PostalArea>
                                         </SpecifiedStructuredAddress>
                                         <SpecifiedContactPerson>
                                            <GivenName>Captain Test</GivenName>
                                            <FamilyName>Captain Test</FamilyName>
                                         </SpecifiedContactPerson>
                                      </SpecifiedContactParty>
                                    </SpecifiedVesselTransportMeans>
                                </ns3:FAReportDocument>
                """;
            catchPart.append(formatNamed(catchWSDL, val));
        }

        Map<String, String> values = Map.of(
                "sendingCountryCode", sendingCountryCode,
                "receivingCountryCode", receivingCountryCode,
                "operationNumber", operationNumber,
                "todtDateString", todtDateString,
                "documentUuidString", documentUuidString,
                "departureUuidString", departureUuidString,
                "catchUuidString", catchUuidString,
                "creationDateTimeElement", creationDateTimeElement,
                "referencedUuidString", referencedUuidString,
                "catchPart", catchPart.toString()
        );


        String wsdlTemplate = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:xeu:bridge-connector:v1">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <urn:Connector2BridgeRequest ON="{operationNumber}" FR="{sendingCountryCode}" AD="{receivingCountryCode}" TODT="{todtDateString}" DF="urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2">
                         <ns3:FLUXFAReportMessage 
                         xmlns="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20" 
                         xmlns:ns2="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20" 
                         xmlns:ns3="urn:un:unece:uncefact:data:standard:FLUXFAReportMessage:3"
                         xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20">
                            <ns3:FLUXReportDocument>
                               <ID schemeID="UUID">{documentUuidString}</ID>
                               {referencedUuidString}
                              {creationDateTimeElement}
                               <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                               <OwnerFLUXParty>
                                  <ID schemeID="FLUX_GP_PARTY">{sendingCountryCode}</ID>
                               </OwnerFLUXParty>
                            </ns3:FLUXReportDocument>
                            <ns3:FAReportDocument>
                               <TypeCode listID="FLUX_FA_REPORT_TYPE">DECLARATION</TypeCode>
                               <AcceptanceDateTime>
                                  <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                               </AcceptanceDateTime>
                               <RelatedFLUXReportDocument>
                                  <ID schemeID="UUID">{departureUuidString}</ID>
                                  <CreationDateTime>
                                     <ns2:DateTime>2022-09-15T10:06:41Z</ns2:DateTime>
                                  </CreationDateTime>
                                  <PurposeCode listID="FLUX_GP_PURPOSE">9</PurposeCode>
                                  <OwnerFLUXParty>
                                     <ID schemeID="FLUX_GP_PARTY">{sendingCountryCode}</ID>
                                  </OwnerFLUXParty>
                               </RelatedFLUXReportDocument>
                               <SpecifiedFishingActivity>
                                  <TypeCode listID="FLUX_FA_TYPE">DEPARTURE</TypeCode>
                                  <OccurrenceDateTime>
                                     <ns2:DateTime>2022-09-15T04:07:00Z</ns2:DateTime>
                                  </OccurrenceDateTime>
                                  <ReasonCode listID="FA_REASON_DEPARTURE">FIS</ReasonCode>
                                  <RelatedFLUXLocation>
                                     <TypeCode listID="FLUX_LOCATION_TYPE">LOCATION</TypeCode>
                                     <CountryID schemeID="TERRITORY">{sendingCountryCode}</CountryID>
                                     <ID schemeID="LOCATION">LTKLJ</ID>
                                  </RelatedFLUXLocation>
                                  <SpecifiedFishingTrip>
                                     <ID schemeID="EU_TRIP_ID">LTU-TRP-101458</ID>
                                  </SpecifiedFishingTrip>
                               </SpecifiedFishingActivity>
                               <SpecifiedVesselTransportMeans>
                                  <ID schemeID="CFR">DNK900001008</ID>
                                  <ID schemeID="IRCS">RD88</ID>
                                  <ID schemeID="EXT_MARK">X108</ID>
                                  <RegistrationVesselCountry>
                                     <ID schemeID="TERRITORY">DNK</ID>
                                  </RegistrationVesselCountry>
                                  <SpecifiedContactParty>
                                     <RoleCode listID="FLUX_CONTACT_ROLE">MASTER</RoleCode>
                                     <SpecifiedStructuredAddress>
                                        <StreetName>Unknown</StreetName>
                                        <CityName>Unknown</CityName>
                                        <CountryID schemeID="TERRITORY">SVN</CountryID>
                                        <PlotIdentification>Unknown</PlotIdentification>
                                        <PostalArea>Unknown</PostalArea>
                                     </SpecifiedStructuredAddress>
                                     <SpecifiedContactPerson>
                                        <GivenName>Captain Test</GivenName>
                                        <FamilyName>Captain Test</FamilyName>
                                     </SpecifiedContactPerson>
                                  </SpecifiedContactParty>
                               </SpecifiedVesselTransportMeans>
                            </ns3:FAReportDocument>
                            
                            
                            {catchPart}
                            
                            
                            
                         </ns3:FLUXFAReportMessage>
                      </urn:Connector2BridgeRequest>
                   </soapenv:Body>
                </soapenv:Envelope>
                """;


        String formattedWsdl = formatNamed(wsdlTemplate, values);
        return formatXML(formattedWsdl);
    }

}
