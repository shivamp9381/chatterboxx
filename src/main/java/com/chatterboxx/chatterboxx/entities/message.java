//package com.chatterboxx.chatterboxx.entities;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//
//@Document(collection = "messages")
//public class message {
//
//    @Id
//    private String id;
//
//    private String roomId;
//    private String sender;
//    private String content;
//    private LocalDateTime timestamp;
//
//    public message() {}
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getRoomId() { return roomId; }
//    public void setRoomId(String roomId) { this.roomId = roomId; }
//
//    public String getSender() { return sender; }
//    public void setSender(String sender) { this.sender = sender; }
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
//}

package com.chatterboxx.chatterboxx.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class message {

    @Id
    private String id;

    private String roomId;
    private String sender;
    private String content;
    private LocalDateTime timestamp;

    // 🔥 NEW
    private boolean seen;

    public message() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}