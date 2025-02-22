package com.task.domain.message;

import com.task.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserMessage extends Message {

    @ManyToOne
    private User receiver;

    public UserMessage(User sender, String message, User receiver) {
        super(sender, message);
        this.receiver = receiver;
    }
}

