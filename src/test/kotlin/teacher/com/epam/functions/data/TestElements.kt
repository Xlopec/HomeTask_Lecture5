package teacher.com.epam.functions.data

import com.epam.functions.JsonArray
import com.epam.functions.JsonObjectBuilder
import com.epam.functions.arr
import com.google.gson.JsonArray as GsonArray
import com.google.gson.JsonElement as GsonElement
import com.google.gson.JsonNull as GsonNull
import com.google.gson.JsonPrimitive as GsonPrimitive

/**
 * Utility class that allows configuring [builder][com.epam.functions.JsonObjectBuilder]
 */
internal sealed class TestElement {
    abstract val field: String
    abstract fun JsonObjectBuilder.applyToBuilder()
    abstract val gsonElement: GsonElement
}

internal data class StringElement(
    override val field: String,
    val value: String?
) : TestElement() {
    override fun JsonObjectBuilder.applyToBuilder() = field by value
    override val gsonElement: GsonElement = value?.let(::GsonPrimitive) ?: GsonNull.INSTANCE
}

internal data class CharElement(
    override val field: String,
    val value: Char?
) : TestElement() {
    override fun JsonObjectBuilder.applyToBuilder() = field by value
    override val gsonElement: GsonElement = value?.let(::GsonPrimitive) ?: GsonNull.INSTANCE
}

internal data class NullElement(
    override val field: String,
) : TestElement() {
    override fun JsonObjectBuilder.applyToBuilder() = field by null
    override val gsonElement: GsonElement = GsonNull.INSTANCE
}

internal data class BooleanElement(
    override val field: String,
    val value: Boolean?
) : TestElement() {
    override fun JsonObjectBuilder.applyToBuilder() = field by value
    override val gsonElement: GsonElement = value?.let(::GsonPrimitive) ?: GsonNull.INSTANCE
}

internal data class NumberElement(
    override val field: String,
    val value: Number?
) : TestElement() {
    override fun JsonObjectBuilder.applyToBuilder() = field by value
    override val gsonElement: GsonElement =
        // We want NaN and both infinities to be represented as strings in a final json
        if (value?.shouldWrapAsDouble() == true || value?.shouldWrapAsFloat() == true) GsonPrimitive(value.toString())
        else value?.let(::GsonPrimitive) ?: GsonNull.INSTANCE
}

internal data class ArrayElement(
    override val field: String,
    val jsonArray: JsonArray,
    override val gsonElement: GsonArray
) : TestElement() {

    constructor(
        field: String,
        rawList: List<Any?>,
        gsonElement: GsonArray
    ) : this(field, arr[rawList], gsonElement)

    override fun JsonObjectBuilder.applyToBuilder() = field by jsonArray
}

private fun Number.shouldWrapAsDouble(): Boolean =
    this is Double && (isNaN() || this == Double.POSITIVE_INFINITY || this == Double.NEGATIVE_INFINITY)

private fun Number.shouldWrapAsFloat(): Boolean =
    this is Float && (isNaN() || this == Float.POSITIVE_INFINITY || this == Float.NEGATIVE_INFINITY)
