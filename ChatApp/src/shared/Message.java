package shared;

import java.io.Serializable;

public class Message implements Serializable {

    private String sender;
    private String text;
    private byte[] imageData;

    public Message(String sender, String text, byte[] imageData) {
        this.sender = sender;
        this.text = text;
        this.imageData = imageData;
    }

    public String getSender() {
        return sender;
    }
    public String getText() {
        return text;
    }

    public byte[] getImageData() {
        return imageData;
    }
}