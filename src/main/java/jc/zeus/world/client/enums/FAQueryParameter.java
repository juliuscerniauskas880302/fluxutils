package jc.zeus.world.client.enums;

public enum FAQueryParameter {
    TRIPID_EU_TRIP_ID("TRIPID", "TRIP", "EU_TRIP_ID"),
    VESSELID_IRCS("VESSELID", "VESSEL", "IRCS"),
    VESSELID_CFR("VESSELID", "VESSEL", "CFR"),
    CONSOLIDATED("CONSOLIDATED", "", "BOOLEAN_TYPE");

    private final String parameter;
    private final String type;
    private final String schemeID;

    FAQueryParameter(String parameter, String type, String schemeID) {
        this.parameter = parameter;
        this.type = type;
        this.schemeID = schemeID;
    }

    public String getParameter() {
        return parameter;
    }

    public String getType() {
        return type;
    }

    public String getSchemeID() {
        return schemeID;
    }
}
