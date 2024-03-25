package edu.java.scrapper.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@Table(schema = "link_tracker_db")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = URIConverter.class)
    private URI url;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;

    @ManyToMany(mappedBy = "links")
    private List<Chat> chats;

    @Converter(autoApply = true)
    static class URIConverter implements AttributeConverter<URI, String> {
        @Override
        public String convertToDatabaseColumn(URI attribute) {
            return attribute.toString();
        }

        @Override
        public URI convertToEntityAttribute(String dbData) {
            return URI.create(dbData);
        }
    }
}
