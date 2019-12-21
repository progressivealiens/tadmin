package com.techsalt.tadmin.model;

public class SubChildrenCommunication {

    String type,text,image;
    boolean isCommunicationFound;

    public SubChildrenCommunication(boolean isCommunicationFound,String type, String text, String image) {
        this.type = type;
        this.text = text;
        this.image = image;
        this.isCommunicationFound = isCommunicationFound;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isCommunicationFound() {
        return isCommunicationFound;
    }

    public void setCommunicationFound(boolean communicationFound) {
        isCommunicationFound = communicationFound;
    }
}
