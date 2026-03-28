//package com.chatterboxx.chatterboxx.controller;
//
//
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.entities.room;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/rooms")
//public class roomController {
//
//    @Autowired
//    roomRepo roomRepo;
//
//    // create room
//    @PostMapping
//    public ResponseEntity<?> createRoom(@RequestBody String roomId) {
//        if(roomRepo.findByRoomId(roomId) != null){
//            // room is already present
//            return ResponseEntity.badRequest().body("Room already exists");
//        }
//        // create new room
//        room room = new room();
//        room.setRoomId(roomId);
//        room savedRoom = roomRepo.save(room);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
//    }
//
//
//    // get room: join
//    @GetMapping("/{roomId}")
//    public ResponseEntity<?> joinRoom(@PathVariable String roomId ){
//        room room = roomRepo.findByRoomId(roomId);
//        if(room == null){
//            return ResponseEntity.badRequest().body("Room does not exist");
//        }
//
//        return ResponseEntity.ok(room);
//    }
//
//    // get messages of room
//    @GetMapping("/{roomId}/message")
//    public ResponseEntity<List<message>>(@PathVariable String roomId, @RequestParam(value = "page", defaultValue = "0", required = false) int page, @RequestParam(value = "size", defaultValue = "20", required = false) int size){
//        room room = roomRepo.findByRoomId(roomId);
//        if (room == null) {
//            return ResponseEntity.badRequest().build();
//        }
//        // get messsages
//        // pagination
//
//        List<message> message = room.getMessages();
//
//        int start = Math.max(0,message.size() - (page + 1)*size);
//        int end = Math.min(start + size, message.size());
//        List<message> paginatedMessages = message.subList(start, end);
//
//        return ResponseEntity.ok(paginatedMessages);
//    }
//
//
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.dto.CreateRoomRequest;
import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.repositories.roomRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class roomController {

    private final roomRepo roomRepo;

    // Constructor Injection (Best Practice)
    public roomController(roomRepo roomRepository) {
        this.roomRepo = roomRepository;
    }

    // ✅ Create Room
//    @PostMapping
//    public ResponseEntity<?> createRoom(@RequestBody String roomId) {
//        if(roomRepo.findByRoomId(roomId) != null){
//            // room is already present
//            return ResponseEntity.badRequest().body("Room already exists");
//        }
//        // create new room
//        room room = new room();
//        room.setRoomId(roomId);
//        room savedRoom = roomRepo.save(room);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
//    }
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {

        String roomId = request.getRoomId().trim();

        if (roomRepo.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        room room = new room();
        room.setRoomId(roomId);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepo.save(room));
    }

    // ✅ Join Room
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room does not exist");
        }

        return ResponseEntity.ok(room);
    }

    // ✅ Get Messages with Pagination
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        List<message> messages = room.getMessages();

        int total = messages.size();
        int start = page * size;

        if (start >= total) {
            return ResponseEntity.ok(List.of());
        }

        int end = Math.min(start + size, total);

        List<message> paginatedMessages = messages.subList(start, end);

        return ResponseEntity.ok(paginatedMessages);
    }
}
