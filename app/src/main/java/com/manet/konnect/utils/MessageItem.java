package com.manet.konnect.utils;

public  class MessageItem {
    private String personName;
    private String message;

    public MessageItem(String personName, String message) {
        this.personName = personName;
        this.message = message;
    }

    public String getPersonName() {
        return personName;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    public void setPersonName(String personName) {
        this.personName = personName;
    }
}