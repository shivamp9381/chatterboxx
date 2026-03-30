package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.payload.MessageRequest;
import com.chatterboxx.chatterboxx.repositories.roomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class chatController {

    @Autowired
    private roomRepo roomRepo;

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

        message message = new message();
        message.setContent(request.getContent());
        message.setSender(request.getSender()); // ✅ Fixed: was getSender()
        message.setTimestamp(LocalDateTime.now());

        room.getMessages().add(message);
        roomRepo.save(room);

        return message;
    }
}