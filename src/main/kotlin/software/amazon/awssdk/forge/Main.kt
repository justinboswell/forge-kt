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
        println("Usage: forge /path/to/interface.kts")
    }

    var declarations = emptyList<KClass<*>>()
    args.forEach { it ->
        val idl = File(it).absoluteFile;
        if (!idl.exists()) {
            println("Input file $idl does not exist")
        }
        val result = compileScript(idl)
        if (result.isError()) {
            result.reports.forEach {
                println(it.toString())
            }
            return
        }
        val script = result.valueOrThrow().returnValue.scriptClass;
        if (script != null)  {
            declarations += script.nestedClasses
            println("Processed $it, found ${script.nestedClasses.map { it.simpleName }}")
        }
    }

    declarations.forEach {
        println("Interface: ${it.simpleName}")
        val annotations = it.annotations
        println("Annotations: $annotations")
    }

}
