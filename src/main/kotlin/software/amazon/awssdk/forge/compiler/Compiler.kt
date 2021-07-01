package software.amazon.awssdk.forge.compiler

import software.amazon.awssdk.forge.native.NativeType
import java.io.File
import java.util.stream.Collectors
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

// Create a script class for processing .forge.kts scripts
@KotlinScript(fileExtension = "forge.kts")
abstract class ForgeIDL

class Compiler(val arch: Architecture = Architecture.Any64) {
    private val scriptingHost = BasicJvmScriptingHost()
    private val scriptCompilationConfiguration = createJvmCompilationConfigurationFromTemplate<ForgeIDL> {
        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    }

    init {
        // Any NativeTypes initialized during this compiler's lifetime will match the arch
        NativeType.arch = arch
    }

    // Create a compilation context and evaluate a script file
    private fun evalScript(file: File): ResultWithDiagnostics<EvaluationResult> {
        return scriptingHost.eval(file.toScriptSource(), scriptCompilationConfiguration, null)
    }

    fun compileScript(file: File): TranslationUnit {
        println("Compiling $file")
        val result = evalScript(file)
        if (result.isError()) {
            throw CompilationFailure(result.reports.joinToString("\n") { it.toString() })
        }

        // Kotlin script creates a class out of the file to scope the contents
        // So we interrogate whatever symbols we can find inside the returned class
        val scriptContext = result.valueOrThrow().returnValue.scriptClass
            ?: throw CompilationFailure("Kotlin script engine could not evaluate $file");

        return TranslationUnit(scriptContext)
    }

    fun compileScripts(paths: Iterable<String>): List<TranslationUnit> {
        return paths.map { pathArg ->
            val path = File(pathArg).absoluteFile;
            if (!path.exists()) {
                throw CompilationFailure("Input path $path does not exist")
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
}