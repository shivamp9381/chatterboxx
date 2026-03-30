//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.entities.room;
//import com.chatterboxx.chatterboxx.payload.MessageRequest;
//import com.chatterboxx.chatterboxx.repositories.MessageRepo;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.*;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Controller
//public class chatController {
//
//    @Autowired
//    private roomRepo roomRepo;
//
//    @Autowired
//    private MessageRepo messageRepo;
//
//    // 🔥 Track online users per room (in-memory)
//    private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();
//
//
//    // ✅ SEND MESSAGE (MongoDB optimized)
//    @MessageMapping("/sendMessage/{roomId}")
//    @SendTo("/topic/room/{roomId}")
//    public message sendMessage(
//            @DestinationVariable String roomId,
//            @Payload MessageRequest request
//    ) {
//
//        // 🔍 Optional safety check
//        room room = roomRepo.findByRoomId(roomId);
//        if (room == null) {
//            throw new RuntimeException("Room not found: " + roomId);
//        }
//
//        // ✅ Create message
//        message msg = new message();
//        msg.setRoomId(roomId);
//        msg.setContent(request.getContent());
//        msg.setSender(request.getSender());
//        msg.setTimestamp(LocalDateTime.now());
//
//        // ✅ Save to MongoDB
//        return messageRepo.save(msg);
//    }
//
//
//    // 🔥 TYPING INDICATOR
//    @MessageMapping("/typing/{roomId}")
//    @SendTo("/topic/typing/{roomId}")
//    public String typing(
//            @DestinationVariable String roomId,
//            @Payload String username
//    ) {
//        return username;
//    }
//
//
//    // 🔥 USER JOIN (Online users)
//    @MessageMapping("/join/{roomId}")
//    @SendTo("/topic/users/{roomId}")
//    public Set<String> join(
//            @DestinationVariable String roomId,
//            @Payload String username
//    ) {
//
//        roomUsers.putIfAbsent(roomId, new HashSet<>());
//        roomUsers.get(roomId).add(username);
//
//        return roomUsers.get(roomId);
//    }
//
//
//    // 🔥 USER LEAVE (important for logout / tab close)
//    @MessageMapping("/leave/{roomId}")
//    @SendTo("/topic/users/{roomId}")
//    public Set<String> leave(
//            @DestinationVariable String roomId,
//            @Payload String username
//    ) {
//
//        if (roomUsers.containsKey(roomId)) {
//            roomUsers.get(roomId).remove(username);
//        }
//
//        return roomUsers.getOrDefault(roomId, new HashSet<>());
//    }
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.payload.MessageRequest;
import com.chatterboxx.chatterboxx.repositories.MessageRepo;
import com.chatterboxx.chatterboxx.repositories.roomRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class chatController {

    @Autowired
    private roomRepo roomRepo;

    @Autowired
    private MessageRepo messageRepo;

    private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    // ✅ SEND MESSAGE
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public message sendMessage(
            @DestinationVariable String roomId,
            @Payload MessageRequest request
    ) {
        message msg = new message();
        msg.setRoomId(roomId);
        msg.setContent(request.getContent());
        msg.setSender(request.getSender());
        msg.setTimestamp(LocalDateTime.now());
        msg.setSeen(false); // 🔥

        return messageRepo.save(msg);
    }

    // 🔥 TYPING
    @MessageMapping("/typing/{roomId}")
    @SendTo("/topic/typing/{roomId}")
    public String typing(@DestinationVariable String roomId,
                         @Payload String username) {
        return username;
    }

    // 🔥 USERS JOIN
    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> join(@DestinationVariable String roomId,
                            @Payload String username) {

        roomUsers.putIfAbsent(roomId, new HashSet<>());
        roomUsers.get(roomId).add(username);

        return roomUsers.get(roomId);
    }

    // 🔥 USERS LEAVE
    @MessageMapping("/leave/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> leave(@DestinationVariable String roomId,
                             @Payload String username) {

        if (roomUsers.containsKey(roomId)) {
            roomUsers.get(roomId).remove(username);
        }

        return roomUsers.getOrDefault(roomId, new HashSet<>());
    }

    // 🔥 READ RECEIPT
    @MessageMapping("/seen/{roomId}")
    @SendTo("/topic/seen/{roomId}")
    public String markSeen(@DestinationVariable String roomId,
                           @Payload String messageId) {

        Optional<message> msg = messageRepo.findById(messageId);

        if (msg.isPresent()) {
            msg.get().setSeen(true);
            messageRepo.save(msg.get());
        }

        return messageId;
    }

    // 🔥 PRIVATE MESSAGE
    @MessageMapping("/private")
    @SendTo("/topic/private")
    public message privateMessage(@Payload message msg) {
        msg.setTimestamp(LocalDateTime.now());
        msg.setSeen(false);
        return messageRepo.save(msg);
    }
}