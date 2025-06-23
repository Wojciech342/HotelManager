package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.message.request.MessageRequest;
import pl.wojtek.project.model.Message;
import pl.wojtek.project.model.User;
import pl.wojtek.project.repository.MessageRepository;
import pl.wojtek.project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final String adminUsername = "admin";

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Message sendMessage(MessageRequest messageRequest) {
        String senderUsername = getCurrentUsername();
        User sender = findUserByUsername(senderUsername);
        User recipient = findUserByUsername(adminUsername);

        String subject = messageRequest.getSubject();
        String content = messageRequest.getContent();

        Message message = new Message(sender, recipient, subject, content);
        return messageRepository.save(message);
    }

    @Transactional
    public Message replyToMessage(Long parentMessageId, MessageRequest messageRequest) {
        String senderUsername = getCurrentUsername();
        User sender = findUserByUsername(senderUsername);
        User adminUser = findUserByUsername(adminUsername);

        Message parentMessage = findMessageById(parentMessageId);

        authorizeAccess(parentMessage, senderUsername);

        // Determine the recipient based on who's sending - always the opposite party
        User recipient = getRecipient(sender, parentMessage, adminUser);

        String subject = formatReplySubject(parentMessage.getSubject());
        String content = messageRequest.getContent();

        Message reply = new Message(sender, recipient, subject, content);
        reply.setParentMessage(parentMessage);

        return messageRepository.save(reply);
    }

    private User getRecipient(User sender, Message parentMessage, User adminUser) {
        User recipient;
        if (sender.getUsername().equals("admin")) {
            if (parentMessage.getSender().getUsername().equals("admin")) {
                recipient = parentMessage.getRecipient(); // User was the first recipient
            } else {
                recipient = parentMessage.getSender(); // User was the first sender
            }
        } else {
            // If sender is regular user, recipient is always admin
            recipient = adminUser;
        }
        return recipient;
    }

    public List<Message> getUserMessages() {
        String currentUsername = getCurrentUsername();
        User user = findUserByUsername(currentUsername);
        return messageRepository.findAllUserMessages(user);
    }

    @Transactional
    public List<Message> getMessageThread(Long messageId) {
        String username = getCurrentUsername();
        Message message = findMessageById(messageId);

        authorizeAccess(message, username);

        // Find the root message of the thread
        while (message.getParentMessage() != null) {
            message = message.getParentMessage();
        }

        List<Message> thread = messageRepository.findByParentMessage(message);
        thread.addFirst(message);

        markThreadMessagesAsRead(thread, username);

        return thread;
    }

    public void markThreadMessagesAsRead(List<Message> thread, String username) {
        for (Message message : thread) {
            System.out.println(message.getRecipient().getUsername());
            if (!message.isRead() && message.getRecipient().getUsername().equals(username)) {
                System.out.println(username);
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    private void authorizeAccess(Message message, String senderUsername) {
        boolean isAuthorized = message.getSender().getUsername().equals(senderUsername) ||
                message.getRecipient().getUsername().equals(senderUsername);

        if (!isAuthorized) {
            throw new AccessDeniedException("You are not authorized to reply to this message");
        }
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Message findMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
    }

    public long getUnreadCount() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return messageRepository.countUnreadMessages(user);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private String formatReplySubject(String subject) {
        return subject.startsWith("Re: ") ? subject : "Re: " + subject;
    }
}