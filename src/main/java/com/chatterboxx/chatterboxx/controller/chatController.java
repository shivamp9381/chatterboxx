//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.entities.room;
//import com.chatterboxx.chatterboxx.payload.MessageRequest;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//import java.time.LocalDateTime;
//
//@Controller
//public class chatController {
//
//    @Autowired
//    private roomRepo roomRepo;
//
//    @MessageMapping("/sendMessage/{roomId}")
//    @SendTo("/topic/room/{roomId}")
//    public message sendMessage(
//            @DestinationVariable String roomId,
//            @Payload MessageRequest request
//    ) {
//        room room = roomRepo.findByRoomId(roomId);
//
//        if (room == null) {
//            throw new RuntimeException("Room not found: " + roomId);
//        }
//
//        message message = new message();
//        message.setContent(request.getContent());
//        message.setSender(request.getSender()); // ✅ Fixed: was getSender()
//        message.setTimestamp(LocalDateTime.now());
//
//        room.getMessages().add(message);
//        roomRepo.save(room);
//
//        return message;
//    }
//}
//
//
//// 🔥 Track users
//private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();
//
//// ✅ SEND MESSAGE (already exists)
//
//// 🔥 TYPING
//@MessageMapping("/typing/{roomId}")
//@SendTo("/topic/typing/{roomId}")
//public String typing(@DestinationVariable String roomId,
//                     @Payload String username) {
//    return username;
//}
//
//// 🔥 USER JOIN
//@MessageMapping("/join/{roomId}")
//@SendTo("/topic/users/{roomId}")
//public Set<String> join(@DestinationVariable String roomId,
//                        @Payload String username) {
//
//    roomUsers.putIfAbsent(roomId, new HashSet<>());
//    roomUsers.get(roomId).add(username);
//
//    return roomUsers.get(roomId);
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.payload.MessageRequest;
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

    // 🔥 Track online users per room
    private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();


    // ✅ SEND MESSAGE (existing functionality)
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public message sendMessage(
            @DestinationVariable String roomId,
            @Payload MessageRequest request
    ) {
        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found: " + roomId);
        }

        message msg = new message();
        msg.setContent(request.getContent());
        msg.setSender(request.getSender());
        msg.setTimestamp(LocalDateTime.now());

        room.getMessages().add(msg);
        roomRepo.save(room);

        return msg;
    }


    // 🔥 TYPING INDICATOR
    @MessageMapping("/typing/{roomId}")
    @SendTo("/topic/typing/{roomId}")
    public String typing(
            @DestinationVariable String roomId,
            @Payload String username
    ) {
        return username;
    }


    // 🔥 USER JOIN (ONLINE USERS TRACKING)
    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> join(
            @DestinationVariable String roomId,
            @Payload String username
    ) {

        roomUsers.putIfAbsent(roomId, new HashSet<>());
        roomUsers.get(roomId).add(username);

        return roomUsers.get(roomId);
    }


    // 🔥 OPTIONAL: USER LEAVE (recommended improvement)
    @MessageMapping("/leave/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> leave(
            @DestinationVariable String roomId,
            @Payload String username
    ) {

        if (roomUsers.containsKey(roomId)) {
            roomUsers.get(roomId).remove(username);
        }

        return roomUsers.getOrDefault(roomId, new HashSet<>());
    }
}