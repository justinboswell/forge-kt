package software.amazon.awssdk.forge

import java.io.File

import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate


import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.jvm.util.isError

import kotlin.reflect.KClass
import kotlin.script.experimental.api.valueOrThrow

// Create a script class for processing .forge.kts scripts
@KotlinScript(fileExtension = "forge.kts")
abstract class ForgeIDL

// Create a compilation context and evaluate a script file
fun compileScript(file: File) : ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ForgeIDL> {
        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    }

    return BasicJvmScriptingHost().eval(file.toScriptSource(), compilationConfiguration, null)
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("Usage: forge <PATH>")
    }

    var declarations = emptyList<KClass<*>>()
    args.forEach { pathArg ->
        val path = File(pathArg).absoluteFile;
        if (!path.exists()) {
            println("Input path $path does not exist")
        }
        var scripts = emptyList<File>()
        if (path.isDirectory()) {
            scripts += path.walkTopDown().filter {
                it.name.endsWith(".forge.kts")
            }.toList()
        } else {
            scripts += path
        }
        scripts.forEach { script ->
            println("Compiling $script")
            val result = compileScript(script)
            if (result.isError()) {
                println("Compilation failed:")
                result.reports.forEach {
                    println(it.toString())
                }
                return
            }
            val scriptContext = result.valueOrThrow().returnValue.scriptClass;
            if (scriptContext != null)  {
                declarations += scriptContext.nestedClasses
                println("Processed $script, found ${scriptContext.nestedClasses.map { it.simpleName }}")
            }
        }
    }

    declarations.forEach {
        println("Interface: ${it.simpleName}")
        val annotations = it.annotations
        println("  Annotations: $annotations")
    }

}
