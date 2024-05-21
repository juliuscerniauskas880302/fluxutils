package jc.zeus.world.client.query;


import jc.zeus.world.client.enums.FAQueryParameter;

public class SimpleFAQueryParameter {
    private final FAQueryParameter faQueryParameter;
    private final String code;

    public SimpleFAQueryParameter(FAQueryParameter faQueryParameter, String code) {
        this.faQueryParameter = faQueryParameter;
        this.code = code;
    }

    public FAQueryParameter getFaQueryParameter() {
        return faQueryParameter;
    }

    public String getCode() {
        return code;
    }
}

