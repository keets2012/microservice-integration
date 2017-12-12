package com.blueskykong.demo.constants;

/**
 * Created by keets on 2017/12/10.
 */
public enum AccessType {

    ACCESS_TYPE_NORMAL("USER", "normalUser"),
    ACCESS_TYPE_TOKENKEY("TOKENKEY", "tokenkeyUser");

    private String description;

    private String type;

    private AccessType(String type, String description) {
        this.description = description;
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
