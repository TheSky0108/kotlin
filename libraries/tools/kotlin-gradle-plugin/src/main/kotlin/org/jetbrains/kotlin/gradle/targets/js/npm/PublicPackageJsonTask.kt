/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.npm

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject.Companion.PACKAGE_JSON
import org.jetbrains.kotlin.gradle.utils.property
import java.io.File
import javax.inject.Inject

open class PublicPackageJsonTask
@Inject
constructor(
    compilation: KotlinJsCompilation
) : DefaultTask() {

    private val npmProject = compilation.npmProject

    private val nodeJs = npmProject.nodeJs

    private val compilationResolution
        get() = nodeJs.npmResolutionManager.requireInstalled()[project][npmProject.compilation]

    @get:Nested
    internal val externalDependencies: Collection<NestedNpmDependency>
        get() = compilationResolution.externalNpmDependencies
            .map {
                NestedNpmDependency(
                    scope = it.scope,
                    name = it.name,
                    version = it.version
                )
            }

    private val realExternalDependencies: Collection<NpmDependency>
        get() = compilationResolution.externalNpmDependencies

    @get:OutputFile
    var packageJsonFile: File by property {
        project.buildDir
            .resolve("tmp")
            .resolve(npmProject.publicPackageJsonTaskName)
            .resolve(PACKAGE_JSON)
    }

    @TaskAction
    fun resolve() {
        packageJson(npmProject, realExternalDependencies).let { packageJson ->

            packageJson.apply {
                listOf(
                    dependencies,
                    peerDependencies,
                    optionalDependencies
                ).forEach { it.processDependencies() }
            }

            packageJson.devDependencies.clear()

            packageJson.saveTo(this@PublicPackageJsonTask.packageJsonFile)
        }
    }

    private fun MutableMap<String, String>.processDependencies() {
        val newDependencies = mutableMapOf<String, String>()
        filterNot { (_, version) -> version.isFileVersion() }
            .forEach { (key, version) ->
                newDependencies[key] = version
            }

        clear()

        newDependencies.forEach { (key, version) ->
            this[key] = version
        }
    }

    companion object {
        const val NAME = "publicPackageJson"
    }
}

internal data class NestedNpmDependency(
    @Input
    val scope: NpmDependency.Scope,
    @Input
    val name: String,
    @Input
    val version: String
)