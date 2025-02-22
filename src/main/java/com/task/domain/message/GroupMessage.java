package com.task.domain.message;

import com.task.domain.Group;
import com.task.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@DiscriminatorValue("GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GroupMessage extends Message {

    @ManyToOne
    private Group recieverGroup;

    public GroupMessage(User sender, String message, Group recieverGroup) {
        super(sender, message);
        this.recieverGroup = recieverGroup;
    }
}
