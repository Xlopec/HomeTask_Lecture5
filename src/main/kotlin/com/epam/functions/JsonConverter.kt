package com.epam.functions

import com.epam.functions.JsonPrimitive.Companion.of

// It's a common trick to expose object with set of operators to get a nicer DSL
@Suppress("ClassName")
object arr {
    operator fun get(
        vararg elements: Any?
    ) = JsonArray.of(elements.map(Any?::toJsonValue))

    operator fun get(
        elements: Iterable<Any?>
    ) = JsonArray.of(elements.map(Any?::toJsonValue))
}

fun obj(
    block: JsonObjectBuilder.() -> Unit
) = JsonObjectBuilder()
    .apply(block)
    .toJsonTree()

@JsonTagMarker
class JsonObjectBuilder {

    private val properties = mutableMapOf<String, JsonValue>()

    infix fun String.by(
        @Suppress("UNUSED_PARAMETER") value: Nothing?
    ) = append(JsonNull)

    infix fun String.by(
        value: String?
    ) = append(value?.let(JsonPrimitive::of) ?: JsonNull)

    infix fun String.by(
        value: Number?
    ) = append(value?.let(JsonPrimitive::of) ?: JsonNull)

    infix fun String.by(
        value: Boolean?
    ) = append(value?.let(JsonPrimitive::of) ?: JsonNull)

    infix fun String.by(
        value: Char?
    ) = append(value?.let(JsonPrimitive::of) ?: JsonNull)

    infix fun String.by(
        value: JsonValue
    ) = append(value)

    infix fun String.by(
        value: Any?
    ) = append(value.toJsonValue())

    fun toJsonTree(): JsonObject = JsonObject.of(properties)

    private fun String.append(
        value: JsonValue
    ) {
        properties[this] = value
    }
}

sealed class JsonValue {
    abstract fun toJsonString(): String
    abstract fun toPrettyJsonString(
        level: Int = 0,
        indent: Int = 1
    ): String
}

object JsonNull : JsonValue() {
    override fun toJsonString() = "null"
    override fun toPrettyJsonString(
        level: Int,
        indent: Int
    ): String = toJsonString()
}

class JsonArray private constructor(
    private val values: List<JsonValue>
) : JsonValue() {

    companion object {

        private val EMPTY = JsonArray(emptyList())

        fun of(
            values: List<JsonValue>
        ) = if (values.isEmpty()) EMPTY else JsonArray(values)
    }

    override fun toJsonString(): String =
        values.joinToString(
            prefix = "[",
            postfix = "]",
            transform = JsonValue::toJsonString
        )

    override fun toPrettyJsonString(
        level: Int,
        indent: Int
    ): String {
        val bracesIndent = '\t'.repeat(level * indent)
        val itemsIndent = '\t'.repeat(level + 1 * indent)

        return values.joinToString(
            prefix = "${makePrefixNewLineIfNeeded(level)}$bracesIndent[\n",
            postfix = "\n$bracesIndent]",
            separator = ",\n",
            transform = { jsonValue -> itemsIndent + jsonValue.toPrettyJsonString(level, indent) }
        )
    }

}

class JsonPrimitive private constructor(
    private val jsonValue: String
) : JsonValue() {

    companion object {

        private val TRUE = JsonPrimitive(true.toString())
        private val FALSE = JsonPrimitive(false.toString())

        fun of(
            value: Number
        ) = JsonPrimitive(value.toString())

        fun of(
            value: String
        ) = JsonPrimitive("\"$value\"")

        fun of(
            value: Char
        ) = of(value.toString())

        fun of(
            value: Boolean
        ) = if (value) TRUE else FALSE
    }

    override fun toJsonString(): String = jsonValue
    override fun toPrettyJsonString(
        level: Int,
        indent: Int
    ): String = toJsonString()
}

class JsonObject private constructor(
    private val properties: Map<String, JsonValue>
) : JsonValue() {

    companion object {

        private val EMPTY = JsonObject(emptyMap())

        fun of(
            properties: Map<String, JsonValue>
        ) = if (properties.isEmpty()) EMPTY else JsonObject(properties)
    }

    override fun toJsonString(): String =
        properties.entries.joinToString(
            prefix = "{",
            postfix = "}",
            transform = Map.Entry<String, JsonValue>::toJsonProperty
        )

    override fun toPrettyJsonString(
        level: Int,
        indent: Int
    ): String {
        val bracesIndent = '\t'.repeat(level * indent)
        val itemsIndent = '\t'.repeat(level * indent)

        return properties.entries.joinToString(
            prefix = "${makePrefixNewLineIfNeeded(level)}$bracesIndent{\n",
            postfix = "\n$bracesIndent}",
            separator = ",\n",
            transform = { entry -> itemsIndent + entry.toJsonProperty(level + 1, indent) }
        )
    }
}

@DslMarker
@Target(AnnotationTarget.CLASS)
private annotation class JsonTagMarker

private fun makePrefixNewLineIfNeeded(
    level: Int
) = "\n".takeUnless { level == 0 } ?: ""

private fun Char.repeat(
    n: Int
): String = toString().repeat(n)

private fun Map.Entry<String, JsonValue>.toJsonProperty(): String =
    "\"$key\": ${value.toJsonString()}"

private fun Map.Entry<String, JsonValue>.toJsonProperty(
    level: Int,
    indent: Int
): String =
    "\"$key\": ${value.toPrettyJsonString(level, indent)}"

private fun Any?.toJsonValue(): JsonValue =
    when (this) {
        is Number -> of(this)
        is String -> of(this)
        is Char -> of(this)
        is Boolean -> of(this)
        is JsonValue -> this
        null -> JsonNull
        else -> of(toString())
    }