package pl.wojtek.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.model.Message;
import pl.wojtek.project.service.MessageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Message>> getUserMessages() {
        String username = getCurrentUsername();
        List<Message> messages = messageService.getUserMessages(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Message> getMessage(@PathVariable("id") Long messageId) {
        Message message = messageService.getMessageById(messageId);

        // Mark message as read when accessed
        if (!message.isRead() && isCurrentUserRecipient(message)) {
            message = messageService.markAsRead(messageId);
        }

        return ResponseEntity.ok(message);
    }

    @GetMapping("/thread/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Message>> getMessageThread(@PathVariable("id") Long messageId) {
        List<Message> thread = messageService.getMessageThread(messageId);
        return ResponseEntity.ok(thread);
    }

    @PostMapping("/contact")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Message> sendContactMessage(@RequestBody Map<String, String> payload) {
        String senderUsername = getCurrentUsername();
        String content = payload.get("message");

        // Set subject using the username instead of provided name
        String subject = "Contact message from " + senderUsername;

        // The recipient is always the admin user
        String adminUsername = "admin";

        Message message = messageService.sendMessage(
                senderUsername,
                adminUsername,
                subject,
                content
        );

        return ResponseEntity.ok(message);
    }

    @PostMapping("/reply/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Message> replyToMessage(
            @PathVariable("id") Long messageId,
            @RequestBody Map<String, String> payload) {

        String senderUsername = getCurrentUsername();
        String content = payload.get("message");

        Message reply = messageService.replyToMessage(messageId, senderUsername, content);
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount() {
        String username = getCurrentUsername();
        long count = messageService.getUnreadCount(username);
        return ResponseEntity.ok(count);
    }

    private boolean isCurrentUserRecipient(Message message) {
        String username = getCurrentUsername();
        return message.getRecipient().getUsername().equals(username);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}