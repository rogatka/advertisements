/*
 * This file is generated by jOOQ.
 */
package com.advertisement.auth.model.jooq.tables


import com.advertisement.auth.model.jooq.Advertisement
import com.advertisement.auth.model.jooq.keys.USERS_ROLES_PKEY
import com.advertisement.auth.model.jooq.keys.USERS_ROLES__ROLE_ID_FK
import com.advertisement.auth.model.jooq.keys.USERS_ROLES__USER_ID_FK
import com.advertisement.auth.model.jooq.tables.Roles.RolesPath
import com.advertisement.auth.model.jooq.tables.Users.UsersPath
import com.advertisement.auth.model.jooq.tables.records.UsersRolesRecord

import java.time.Instant
import java.util.UUID

import kotlin.collections.Collection
import kotlin.collections.List

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.Path
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class UsersRoles(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, UsersRolesRecord>?,
    parentPath: InverseForeignKey<out Record, UsersRolesRecord>?,
    aliased: Table<UsersRolesRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<UsersRolesRecord>(
    alias,
    Advertisement.ADVERTISEMENT,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>advertisement.users_roles</code>
         */
        val USERS_ROLES: UsersRoles = UsersRoles()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<UsersRolesRecord> = UsersRolesRecord::class.java

    /**
     * The column <code>advertisement.users_roles.id</code>.
     */
    val ID: TableField<UsersRolesRecord, UUID?> = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>advertisement.users_roles.user_id</code>.
     */
    val USER_ID: TableField<UsersRolesRecord, UUID?> = createField(DSL.name("user_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>advertisement.users_roles.role_id</code>.
     */
    val ROLE_ID: TableField<UsersRolesRecord, Long?> = createField(DSL.name("role_id"), SQLDataType.BIGINT.nullable(false), this, "")

    /**
     * The column <code>advertisement.users_roles.created_at</code>.
     */
    val CREATED_AT: TableField<UsersRolesRecord, Instant?> = createField(DSL.name("created_at"), SQLDataType.INSTANT.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<UsersRolesRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<UsersRolesRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<UsersRolesRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>advertisement.users_roles</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>advertisement.users_roles</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>advertisement.users_roles</code> table reference
     */
    constructor(): this(DSL.name("users_roles"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, UsersRolesRecord>?, parentPath: InverseForeignKey<out Record, UsersRolesRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, USERS_ROLES, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class UsersRolesPath : UsersRoles, Path<UsersRolesRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, UsersRolesRecord>?, parentPath: InverseForeignKey<out Record, UsersRolesRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<UsersRolesRecord>): super(alias, aliased)
        override fun `as`(alias: String): UsersRolesPath = UsersRolesPath(DSL.name(alias), this)
        override fun `as`(alias: Name): UsersRolesPath = UsersRolesPath(alias, this)
        override fun `as`(alias: Table<*>): UsersRolesPath = UsersRolesPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Advertisement.ADVERTISEMENT
    override fun getPrimaryKey(): UniqueKey<UsersRolesRecord> = USERS_ROLES_PKEY
    override fun getReferences(): List<ForeignKey<UsersRolesRecord, *>> = listOf(USERS_ROLES__ROLE_ID_FK, USERS_ROLES__USER_ID_FK)

    private lateinit var _roles: RolesPath

    /**
     * Get the implicit join path to the <code>advertisement.roles</code> table.
     */
    fun roles(): RolesPath {
        if (!this::_roles.isInitialized)
            _roles = RolesPath(this, USERS_ROLES__ROLE_ID_FK, null)

        return _roles;
    }

    val roles: RolesPath
        get(): RolesPath = roles()

    private lateinit var _users: UsersPath

    /**
     * Get the implicit join path to the <code>advertisement.users</code> table.
     */
    fun users(): UsersPath {
        if (!this::_users.isInitialized)
            _users = UsersPath(this, USERS_ROLES__USER_ID_FK, null)

        return _users;
    }

    val users: UsersPath
        get(): UsersPath = users()
    override fun `as`(alias: String): UsersRoles = UsersRoles(DSL.name(alias), this)
    override fun `as`(alias: Name): UsersRoles = UsersRoles(alias, this)
    override fun `as`(alias: Table<*>): UsersRoles = UsersRoles(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): UsersRoles = UsersRoles(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): UsersRoles = UsersRoles(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): UsersRoles = UsersRoles(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): UsersRoles = UsersRoles(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): UsersRoles = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): UsersRoles = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): UsersRoles = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): UsersRoles = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): UsersRoles = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): UsersRoles = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): UsersRoles = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): UsersRoles = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): UsersRoles = where(DSL.notExists(select))
}
