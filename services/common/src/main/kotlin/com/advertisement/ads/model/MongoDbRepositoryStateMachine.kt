package com.advertisement.ads.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "state_machine")
data class MongoDbRepositoryStateMachine(
    var id: String? = null,
    var machineId: String? = null,
    var state: String? = null,
    var stateMachineContext: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MongoDbRepositoryStateMachine

        if (id != other.id) return false
        if (machineId != other.machineId) return false
        if (state != other.state) return false
        if (stateMachineContext != null) {
            if (other.stateMachineContext == null) return false
            if (!stateMachineContext.contentEquals(other.stateMachineContext)) return false
        } else if (other.stateMachineContext != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (machineId?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + (stateMachineContext?.contentHashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MongoDbRepositoryStateMachine(id=$id, machineId=$machineId, state=$state, stateMachineContext=${stateMachineContext?.contentToString()})"
    }
}
