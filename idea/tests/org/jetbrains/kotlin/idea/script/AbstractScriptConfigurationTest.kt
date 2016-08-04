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

package org.jetbrains.kotlin.idea.script

import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.PlatformTestUtil
import org.jetbrains.kotlin.checkers.AbstractPsiCheckerTest
import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime
import org.jetbrains.kotlin.idea.core.script.KotlinScriptConfigurationManager
import org.jetbrains.kotlin.script.ScriptTemplateProvider
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.MockLibraryUtil
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File

abstract class AbstractScriptConfigurationTest : AbstractPsiCheckerTest() {
    override fun doTest(path: String) {


        val templateOutDir = KotlinTestUtils.tmpDir("templateOut")
        MockLibraryUtil.compileKotlin(
                "${path}template", templateOutDir,
                PathUtil.getKotlinPathsForDistDirectory().compilerPath.canonicalPath
        )

        registerScriptTemplateProvider(templateOutDir)

        val vFile = LocalFileSystem.getInstance().findFileByPath("${path}script.kts")!!
        super.doTest(vFile)
    }

    private fun registerScriptTemplateProvider(dir: File) {
        PlatformTestUtil.registerExtension(Extensions.getArea(project), ScriptTemplateProvider.EP_NAME, TestScriptTemplateProvider(dir), myTestRootDisposable)
        KotlinScriptConfigurationManager.getInstance(project).reloadScriptDefinitions()
    }
}

class TestScriptTemplateProvider(val dir: File) : ScriptTemplateProvider {
    override val id = "Test"
    override val version = 0
    override val isValid = true
    override val templateClassName = "custom.scriptDefinition.Template"
    override val dependenciesClasspath = listOf(dir.canonicalPath)
    override val environment = mapOf("kotlin-runtime" to ForTestCompileRuntime.runtimeJarForTests())
}