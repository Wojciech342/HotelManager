package pl.wojtek.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.message.request.MessageRequest;
import pl.wojtek.project.model.Message;
import pl.wojtek.project.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest messageRequest) {
        Message message = messageService.sendMessage(messageRequest);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/reply/{id}")
    public ResponseEntity<Message> replyToMessage(
            @PathVariable("id") Long messageId,
            @RequestBody MessageRequest messageRequest) {
        Message reply = messageService.replyToMessage(messageId, messageRequest);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getUserMessages() {
        List<Message> messages = messageService.getUserMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/thread/{id}")
    public ResponseEntity<List<Message>> getMessageThread(@PathVariable("id") Long messageId) {
        List<Message> thread = messageService.getMessageThread(messageId);
        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        long count = messageService.getUnreadCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}