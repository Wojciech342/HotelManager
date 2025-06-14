package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wojtek.project.model.Message;
import pl.wojtek.project.model.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrderBySentAtDesc(User sender);
    List<Message> findByRecipientOrderBySentAtDesc(User recipient);

    @Query("SELECT m FROM Message m WHERE m.sender = ?1 OR m.recipient = ?1 ORDER BY m.sentAt DESC")
    List<Message> findAllUserMessages(User user);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient = ?1 AND m.read = false")
    long countUnreadMessages(User user);

    List<Message> findByParentMessage(Message parentMessage);
}