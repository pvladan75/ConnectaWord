package com.program.connectaword.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SessionSerializer : Serializer<SessionData> {
    override val defaultValue: SessionData = SessionData()

    override suspend fun readFrom(input: InputStream): SessionData {
        return try {
            Json.decodeFromString(
                deserializer = SessionData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: SessionData, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = SessionData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}