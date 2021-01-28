@file:Suppress("TestFunctionName")

package teacher.com.epam.functions

import io.kotlintest.matchers.withClue
import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import teacher.com.epam.functions.data.*
import teacher.com.epam.functions.data.parseThrowing
import com.google.gson.JsonObject as GsonObject

private typealias TestData = Pair<String, Iterable<TestElement>>

@RunWith(value = Parameterized::class)
internal class ParameterizedObjectDslTest(
    private val testData: TestData,
) {
    @Test
    fun `when creating object via DSL, given test dataset, then resulting json is valid and contains expected data`() {
        val (testName, data) = testData

        withClue("Failed test for a dataset: \"$testName\"") {
            parseThrowing<GsonObject>(JsonObject(data)) shouldBe GsonObject(data)
        }
    }

    private companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun testDataset() = listOf(
            "null elements test" to testNullData(),
            "string elements tests" to testStringData(),
            "integer elements tests" to testIntegralNumbersData(),
            "float elements tests" to testFloatData(),
            "double elements tests" to testDoubleData(),
            "boolean elements tests" to testBooleanData(),
            "array elements tests" to testArrayData()
        ).onEach { (_, data) -> checkForDuplicates(data) }
    }
}

private fun checkForDuplicates(
    elements: Collection<TestElement>
) =
    check(elements.groupBy(TestElement::field).values.all(Collection<*>::isSingleton)) {
        "Found duplicates: ${elements.groupBy(TestElement::field).filter { (_, l) -> l.size > 1 }}"
    }

private inline val Collection<*>.isSingleton get() = size == 1