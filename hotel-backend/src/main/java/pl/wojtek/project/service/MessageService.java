package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(String senderUsername, String recipientUsername, String subject, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = new Message(sender, recipient, subject, content);
        return messageRepository.save(message);
    }

    public Message replyToMessage(Long parentMessageId, String senderUsername, String content) {
        Message parentMessage = messageRepository.findById(parentMessageId)
                .orElseThrow(() -> new RuntimeException("Original message not found"));

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Find the admin user
        User adminUser = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Determine the recipient based on who's sending - always the opposite party
        User recipient;
        if (sender.getUsername().equals("admin")) {
            // If sender is admin, find the user in the conversation
            if (parentMessage.getSender().getUsername().equals("admin")) {
                recipient = parentMessage.getRecipient(); // Admin wrote to this user
            } else {
                recipient = parentMessage.getSender(); // This user wrote to admin
            }
        } else {
            // If sender is regular user, recipient is always admin
            recipient = adminUser;
        }

        // Use the same subject with "Re: " prefix if it doesn't already have it
        String subject = parentMessage.getSubject();
        if (!subject.startsWith("Re: ")) {
            subject = "Re: " + subject;
        }

        Message reply = new Message(sender, recipient, subject, content);
        reply.setParentMessage(parentMessage);

        return messageRepository.save(reply);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public List<Message> getUserMessages(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.findAllUserMessages(user);
    }

    public List<Message> getMessageThread(Long messageId) {
        Message rootMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // If this is a reply, find the root message
        while (rootMessage.getParentMessage() != null) {
            rootMessage = rootMessage.getParentMessage();
        }

        // Get all messages in this thread (replies to this message)
        List<Message> thread = messageRepository.findByParentMessage(rootMessage);
        // Add the root message at the beginning
        thread.add(0, rootMessage);

        return thread;
    }

    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        return messageRepository.save(message);
    }

    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.countUnreadMessages(user);
    }

    public User getAdminUser() {
        return userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }
}