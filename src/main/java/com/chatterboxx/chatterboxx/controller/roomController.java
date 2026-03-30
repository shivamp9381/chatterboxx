//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.dto.CreateRoomRequest;
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.entities.room;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/rooms")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
//public class roomController {
//
//    private final roomRepo roomRepo;
//
//    public roomController(roomRepo roomRepository) {
//        this.roomRepo = roomRepository;
//    }
//
//    @PostMapping
//    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
//        String roomId = request.getRoomId().trim();
//
//        if (roomRepo.findByRoomId(roomId) != null) {
//            return ResponseEntity.badRequest().body("Room already exists");
//        }
//
//        room room = new room();
//        room.setRoomId(roomId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepo.save(room));
//    }
//
//    @GetMapping("/{roomId}")
//    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
//        room room = roomRepo.findByRoomId(roomId);
//
//        if (room == null) {
//            return ResponseEntity.badRequest().body("Room does not exist");
//        }
//
//        return ResponseEntity.ok(room);
//    }
//
//    @GetMapping("/{roomId}/messages")
//    public ResponseEntity<List<message>> getMessages(
//            @PathVariable String roomId,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "20") int size
//    ) {
//        room room = roomRepo.findByRoomId(roomId);
//
//        if (room == null) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        List<message> messages = room.getMessages();
//        int total = messages.size();
//        int start = page * size;
//
//        if (start >= total) {
//            return ResponseEntity.ok(List.of());
//        }
//
//        int end = Math.min(start + size, total);
//        return ResponseEntity.ok(messages.subList(start, end));
//    }
//}



package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.dto.CreateRoomRequest;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.repositories.roomRepo;
import com.chatterboxx.chatterboxx.repositories.MessageRepo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class roomController {

    private final roomRepo roomRepo;
    private final MessageRepo messageRepo;

    public roomController(roomRepo roomRepo, MessageRepo messageRepo) {
        this.roomRepo = roomRepo;
        this.messageRepo = messageRepo;
    }

    // ✅ CREATE ROOM
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {

        String roomId = request.getRoomId().trim();

        if (roomId.isEmpty()) {
            return ResponseEntity.badRequest().body("Room ID cannot be empty");
        }

        if (roomRepo.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        room room = new room();
        room.setRoomId(roomId);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepo.save(room));
    }

    // ✅ JOIN ROOM
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room does not exist");
        }

        return ResponseEntity.ok(room);
    }

    // ✅ GET MESSAGES (optimized - separate collection)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String roomId) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        List<message> messages = messageRepo.findByRoomIdOrderByTimestampAsc(roomId);

        return ResponseEntity.ok(messages);
    }
}