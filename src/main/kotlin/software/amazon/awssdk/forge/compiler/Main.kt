package software.amazon.awssdk.forge.compiler

import java.io.File
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

    val context = Context()

    var classes = emptyList<KClass<*>>()
    var functions = emptyList<KCallable<*>>()
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
                // Extract interfaces/classes
                val scriptClasses = scriptContext.nestedClasses
                classes += scriptClasses
                println("  Classes:")
                scriptClasses.map { it.simpleName }.forEach {
                    println("    $it")
                }

                // Extract functions
                val scriptFunctions = scriptContext.members.filter { fn ->
                    !setOf("equals", "hashCode", "toString").contains(fn.name)
                }
                functions += scriptFunctions
                println("  Functions:")
                scriptFunctions.map { fn ->
                    fn.name
                }.forEach {
                    println("    $it")
                }
            }
        }
    }

    // Convert from Kotlin reflection to our representation
    context.structs += classes.associate {
        it.simpleName!! to Struct(it)
    }
    context.symbols += functions.associate {
        it.name to NativeFunction(it)
    }

    context.structs.forEach { struct ->
        println("struct $struct")
    }

    context.symbols.forEach { entry ->
        val fn = entry.value as NativeFunction
        println("symbol ${fn.cdecl}")
    }
}
