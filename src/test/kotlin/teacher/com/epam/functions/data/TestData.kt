@file:Suppress("TestFunctionName")

package teacher.com.epam.functions.data

import com.epam.functions.arr
import com.epam.functions.obj
import com.google.gson.JsonArray
import teacher.com.epam.functions.*

internal fun testNullData(): List<NullElement> =
    listOf(NullElement("null_field"))

internal fun testStringData(): List<TestElement> =
    listOf(
        StringElement("string_field", "string"),
        StringElement("empty_string_field", ""),
    )

internal fun testIntegralNumbersData() = listOf(
    // int
    NumberElement("some_int_field", 10),
    NumberElement("max_int_field", Int.MAX_VALUE),
    NumberElement("min_int_field", Int.MIN_VALUE),
    NumberElement("negative_int_field", -10),
    // long
    NumberElement("some_long_field", 20L),
    NumberElement("max_long_field", Long.MAX_VALUE),
    NumberElement("min_long_field", Long.MIN_VALUE),
    NumberElement("negative_long_field", -20L),
    // short
    NumberElement("some_short_field", 30.toShort()),
    NumberElement("max_short_field", Short.MAX_VALUE),
    NumberElement("min_short_field", Short.MIN_VALUE),
    NumberElement("negative_short_field", (-30).toShort()),
    // byte
    NumberElement("some_byte_field", 40.toByte()),
    NumberElement("max_byte_field", Byte.MAX_VALUE),
    NumberElement("min_byte_field", Byte.MIN_VALUE),
    NumberElement("negative_byte_field", (-40).toByte()),
)

internal fun testFloatData() = listOf(
    NumberElement("some_float_field", 2f),
    NumberElement("negative_float_field", -2f),
)

internal fun testDoubleData() = listOf(
    NumberElement("positive_double_field", 6.0),
    NumberElement("negative_double_field", -6.0),
    NumberElement("max_double_field", Double.MAX_VALUE),
    NumberElement("min_double_field", Double.MIN_VALUE),
    NumberElement("nan_double_field", Double.NaN),
    NumberElement("positive_inf_double_field", Double.POSITIVE_INFINITY),
    NumberElement("negative_inf_double_field", Double.NEGATIVE_INFINITY),
)

internal fun testBooleanData(): List<BooleanElement> = listOf(
    BooleanElement("true_field", true),
    BooleanElement("false_field", false),
)

@Suppress("CAST_NEVER_SUCCEEDS")
internal fun testArrayData(): List<ArrayElement> {

    val testAny = Any()
    val testStringData = testStringData()
    val rawValues = listOf(
        null,
        "acdef",
        'a',
        1,
        2.0,
        3f,
        true,
        false,
        testAny,
        obj { },
        JsonObject(testStringData),
        arr[emptyList()],
        arr[null, "ghijk", 'b', 3, 4.0, 5f, false, true, testAny]
    )

    val gsonValues = GsonArray {
        add(null as? String)
        add("acdef")
        add('a')
        add(1)
        add(2.0)
        add(3f)
        add(true)
        add(false)
        add(testAny.toString())
        add(com.google.gson.JsonObject())
        add(GsonObject(testStringData))
        add(JsonArray())
        add(GsonArray {
            add(null as? String)
            add("ghijk")
            add('b')
            add(3)
            add(4.0)
            add(5f)
            add(false)
            add(true)
            add(testAny.toString())
        })
    }

    return listOf(
        ArrayElement("empty_array_field", listOf(), JsonArray()),
        ArrayElement("array_field", rawValues, gsonValues)
    )
}

internal fun JsonObject(
    elements: Iterable<TestElement>
) = obj { elements.forEach { element -> with(element) { applyToBuilder() } } }

internal fun GsonObject(
    elements: Iterable<TestElement>
) = com.google.gson.JsonObject()
    .apply { elements.forEach { element -> add(element.field, element.gsonElement) } }

internal fun GsonArray(
    block: JsonArray.() -> Unit
) = JsonArray().apply(block)