package com.chatterboxx.chatterboxx.entities;

import java.time.LocalDateTime;

public class message {

    private String sender;
    private String content;
    private LocalDateTime timestamp;

    public message() {}

    public message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // ✅ Fixed: getSender() takes no parameters
    public String getSender() { return this.sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}