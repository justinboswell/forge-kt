package software.amazon.awssdk.forge.compiler

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.stream.Collectors
import kotlin.reflect.KCallable

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
import kotlin.system.exitProcess

// Create a script class for processing .forge.kts scripts
@KotlinScript(fileExtension = "forge.kts")
abstract class ForgeIDL

// Create a compilation context and evaluate a script file
fun evalScript(file: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ForgeIDL> {
        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    }

    return BasicJvmScriptingHost().eval(file.toScriptSource(), compilationConfiguration, null)
}

fun compileScript(file: File): TranslationUnit {
    val ctx = TranslationUnit()
    println("Compiling $file")
    val result = evalScript(file)
    if (result.isError()) {
        throw CompilationFailure(result.reports.joinToString("\n") { it.toString() })
    }

    // Kotlin script creates a class out of the file to scope the contents
    // So we interrogate whatever symbols we can find inside the returned class
    val scriptContext = result.valueOrThrow().returnValue.scriptClass;
    if (scriptContext != null) {
        // Extract interfaces/classes
        val scriptClasses = scriptContext.nestedClasses

        // Extract functions
        val scriptFunctions = scriptContext.members.filter { fn ->
            !setOf("equals", "hashCode", "toString").contains(fn.name)
        }

        // Convert from Kotlin reflection to our representation
        ctx.structs += scriptClasses.associate {
            it.simpleName!! to Struct(it)
        }
        ctx.symbols += scriptFunctions.associate {
            it.name to NativeFunction(it)
        }
    }

    return ctx;
}

fun compileSources(paths: Iterable<String>): List<TranslationUnit> {
    return paths.map { pathArg ->
        val path = File(pathArg).absoluteFile;
        if (!path.exists()) {
            println("Input path $path does not exist")
        }
        val scripts = emptyList<File>().toMutableList()
        if (path.isDirectory) {
            scripts += path.walkTopDown().filter {
                it.name.endsWith(".forge.kts")
            }.toList()
        } else {
            scripts += path
        }
        scripts.stream().parallel()
            .map { compileScript(it) }
            .collect(Collectors.toList())
    }.flatten()
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("Usage: forge <PATH>")
    }

    val paths = args.toList()
    try {
        val translationUnits = compileSources(paths)
        translationUnits.forEach { tu ->
            tu.structs.values.forEach { struct ->
                println("struct $struct")
            }
            tu.symbols.values.forEach { symbol ->
                println("symbol ${(symbol as NativeFunction).cdecl}")
            }
        }
    } catch (compileFailure: CompilationFailure) {
        println("Compilation failed:")
        println(compileFailure.message)
        exitProcess(-1)
    }
}
