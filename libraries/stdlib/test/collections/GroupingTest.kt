package test.collections

import org.junit.Test
import kotlin.test.*

class GroupingTest {

    @Test fun groupingProducers() {

        fun <T, K> verifyGrouping(grouping: Grouping<T, K>, expectedElements: List<T>, expectedKeys: List<K>) {
            val elements = grouping.elementIterator().asSequence().toList()
            val keys = elements.map(grouping::keySelector)

            assertEquals(expectedElements, elements)
            assertEquals(expectedKeys, keys)
        }

        val elements = listOf("foo", "bar", "value", "x")
        val keySelector = String::length
        val keys = elements.map(keySelector)

        fun verifyGrouping(grouping: Grouping<String, Int>) = verifyGrouping(grouping, elements, keys)

        verifyGrouping(elements.groupingBy { it.length })
        verifyGrouping(elements.toTypedArray().groupingBy(keySelector))
        verifyGrouping(elements.asSequence().groupingBy(keySelector))

        val charSeq = "some sequence of chars"
        verifyGrouping(charSeq.groupingBy(Char::toInt), charSeq.toList(), charSeq.map(Char::toInt))
    }


    @Test fun countEach() {

    }
}