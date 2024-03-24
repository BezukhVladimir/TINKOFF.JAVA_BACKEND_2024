/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.repositories.jooq.link_tracker_db.tables.pojos;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.13"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ChatsLinks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idChat;
    private Long idLink;

    public ChatsLinks() {}

    public ChatsLinks(ChatsLinks value) {
        this.idChat = value.idChat;
        this.idLink = value.idLink;
    }

    @ConstructorProperties({ "idChat", "idLink" })
    public ChatsLinks(
        @NotNull Long idChat,
        @NotNull Long idLink
    ) {
        this.idChat = idChat;
        this.idLink = idLink;
    }

    /**
     * Getter for <code>LINK_TRACKER_DB.CHATS_LINKS.ID_CHAT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getIdChat() {
        return this.idChat;
    }

    /**
     * Setter for <code>LINK_TRACKER_DB.CHATS_LINKS.ID_CHAT</code>.
     */
    public void setIdChat(@NotNull Long idChat) {
        this.idChat = idChat;
    }

    /**
     * Getter for <code>LINK_TRACKER_DB.CHATS_LINKS.ID_LINK</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getIdLink() {
        return this.idLink;
    }

    /**
     * Setter for <code>LINK_TRACKER_DB.CHATS_LINKS.ID_LINK</code>.
     */
    public void setIdLink(@NotNull Long idLink) {
        this.idLink = idLink;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ChatsLinks other = (ChatsLinks) obj;
        if (this.idChat == null) {
            if (other.idChat != null)
                return false;
        }
        else if (!this.idChat.equals(other.idChat))
            return false;
        if (this.idLink == null) {
            if (other.idLink != null)
                return false;
        }
        else if (!this.idLink.equals(other.idLink))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.idChat == null) ? 0 : this.idChat.hashCode());
        result = prime * result + ((this.idLink == null) ? 0 : this.idLink.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ChatsLinks (");

        sb.append(idChat);
        sb.append(", ").append(idLink);

        sb.append(")");
        return sb.toString();
    }
}
