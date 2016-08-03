/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.annotation.processing.test.processor

import org.jetbrains.kotlin.java.model.elements.JeMethodExecutableElement
import org.jetbrains.kotlin.java.model.elements.JeTypeElement
import org.jetbrains.kotlin.java.model.elements.JeVariableElement
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

class ProcessorTests : AbstractProcessorTest() {
    override val testDataDir = "plugins/annotation-processing/testData/processors"
    
    fun testSimple() = test("Simple", "Anno") { set, roundEnv, env ->
        assertEquals(1, set.size)
        val annotated = roundEnv.getElementsAnnotatedWith(set.first())
        assertEquals(3, annotated.size)
        
        val clazz = annotated.firstIsInstance<JeTypeElement>()
        val method = annotated.firstIsInstance<JeMethodExecutableElement>()
        val field = annotated.firstIsInstance<JeVariableElement>()
        
        listOf(clazz, method, field).forEach {
            it.assertHasAnnotation("Anno", it.simpleName.toString() + "Anno")
        }
    }
    
    fun testSeveralAnnotations() = test("SeveralAnnotations", "Name", "Age") { set, roundEnv, env ->
        val annos = set.toList()
        
        assertEquals(2, annos.size)
        val annotated1 = roundEnv.getElementsAnnotatedWith(annos[0]).sortedBy { it.simpleName.toString() }
        val annotated2 = roundEnv.getElementsAnnotatedWith(annos[1]).sortedBy { it.simpleName.toString() }
        assertEquals(2, annotated1.size)
        assertEquals(annotated1, annotated2)
        
        val (kate, mary) = Pair(annotated1[0] as JeTypeElement, annotated1[1] as JeTypeElement)
        assertEquals("Mary", mary.simpleName)
        with (kate) {
            assertEquals("Kate", simpleName)
            assertHasAnnotation("Name", "Kate")
            assertHasAnnotation("Age", 22)
        }
    }

    fun testStar() = test("Star", "*") { set, roundEnv, env ->
        assertEquals(true, set.any { it.qualifiedName.toString() == "Anno" })
    }
    
    fun testStar2() = test("Star", "Anno", "*") { set, roundEnv, env ->
        assertEquals(true, set.any { it.qualifiedName.toString() == "Anno" })
    }
    
    fun testDoesNotRun() = testShouldNotRun("DoesNotRun", "Anno")
    fun testDoesNotRun2() = testShouldNotRun("DoesNotRun2", "Anno")
}


