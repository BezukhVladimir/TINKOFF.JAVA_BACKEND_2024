package edu.java.scrapper.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@Table(schema = "link_tracker_db")
public class Chat {
    @Id
    @NotNull
    private Long id;

    @Column(name = "created_at")
    @NotNull
    private OffsetDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "chats_links",
        joinColumns =        @JoinColumn(name = "id_chat"),
        inverseJoinColumns = @JoinColumn(name = "id_link")
    )

    private List<Link> links;
}
