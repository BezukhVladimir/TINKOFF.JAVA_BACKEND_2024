/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.repositories.jooq.link_tracker_db.tables;

import edu.java.scrapper.repositories.jooq.link_tracker_db.Keys;
import edu.java.scrapper.repositories.jooq.link_tracker_db.LinkTrackerDb;
import edu.java.scrapper.repositories.jooq.link_tracker_db.tables.records.ChatsLinksRecord;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


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
public class ChatsLinks extends TableImpl<ChatsLinksRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>LINK_TRACKER_DB.CHATS_LINKS</code>
     */
    public static final ChatsLinks CHATS_LINKS = new ChatsLinks();

    /**
     * The class holding records for this type
     */
    @Override
    @NotNull
    public Class<ChatsLinksRecord> getRecordType() {
        return ChatsLinksRecord.class;
    }

    /**
     * The column <code>LINK_TRACKER_DB.CHATS_LINKS.ID_CHAT</code>.
     */
    public final TableField<ChatsLinksRecord, Long> ID_CHAT = createField(DSL.name("ID_CHAT"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>LINK_TRACKER_DB.CHATS_LINKS.ID_LINK</code>.
     */
    public final TableField<ChatsLinksRecord, Long> ID_LINK = createField(DSL.name("ID_LINK"), SQLDataType.BIGINT.nullable(false), this, "");

    private ChatsLinks(Name alias, Table<ChatsLinksRecord> aliased) {
        this(alias, aliased, null);
    }

    private ChatsLinks(Name alias, Table<ChatsLinksRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>LINK_TRACKER_DB.CHATS_LINKS</code> table
     * reference
     */
    public ChatsLinks(String alias) {
        this(DSL.name(alias), CHATS_LINKS);
    }

    /**
     * Create an aliased <code>LINK_TRACKER_DB.CHATS_LINKS</code> table
     * reference
     */
    public ChatsLinks(Name alias) {
        this(alias, CHATS_LINKS);
    }

    /**
     * Create a <code>LINK_TRACKER_DB.CHATS_LINKS</code> table reference
     */
    public ChatsLinks() {
        this(DSL.name("CHATS_LINKS"), null);
    }

    public <O extends Record> ChatsLinks(Table<O> child, ForeignKey<O, ChatsLinksRecord> key) {
        super(child, key, CHATS_LINKS);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : LinkTrackerDb.LINK_TRACKER_DB;
    }

    @Override
    @NotNull
    public UniqueKey<ChatsLinksRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_34D;
    }

    @Override
    @NotNull
    public List<ForeignKey<ChatsLinksRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CONSTRAINT_3, Keys.CONSTRAINT_34);
    }

    private transient Chat _chat;
    private transient Link _link;

    /**
     * Get the implicit join path to the <code>LINK_TRACKER_DB.CHAT</code>
     * table.
     */
    public Chat chat() {
        if (_chat == null)
            _chat = new Chat(this, Keys.CONSTRAINT_3);

        return _chat;
    }

    /**
     * Get the implicit join path to the <code>LINK_TRACKER_DB.LINK</code>
     * table.
     */
    public Link link() {
        if (_link == null)
            _link = new Link(this, Keys.CONSTRAINT_34);

        return _link;
    }

    @Override
    @NotNull
    public ChatsLinks as(String alias) {
        return new ChatsLinks(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public ChatsLinks as(Name alias) {
        return new ChatsLinks(alias, this);
    }

    @Override
    @NotNull
    public ChatsLinks as(Table<?> alias) {
        return new ChatsLinks(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public ChatsLinks rename(String name) {
        return new ChatsLinks(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public ChatsLinks rename(Name name) {
        return new ChatsLinks(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public ChatsLinks rename(Table<?> name) {
        return new ChatsLinks(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Long, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Long, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}