@file:Suppress("RemoveExplicitTypeArguments", "TestFunctionName")

package teacher.com.epam.functions

import com.epam.functions.arr
import com.epam.functions.obj
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import teacher.com.epam.functions.data.parseThrowing
import teacher.com.epam.functions.data.testArrayData
import com.google.gson.JsonArray as GsonArray

@RunWith(JUnit4::class)
internal class DslTest {
    @Test
    fun `when creating object via DSL, given empty object DSL, then empty json object produced`() =
        parseThrowing<Map<*, *>>(obj { }) shouldContainExactly emptyMap()

    @Test
    fun `when creating array via DSL, given empty iterable, then empty json array produced`() =
        parseThrowing<List<*>>(arr[emptyList()]) shouldContainExactly emptyList()

    @Test
    fun `when creating array via DSL, given non empty, then valid json array produced`() =
        testArrayData()
            .forEach { data -> parseThrowing<GsonArray>(data.jsonArray) shouldBe data.gsonElement }
}