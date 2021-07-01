package software.amazon.awssdk.forge.compiler

import kotlin.system.exitProcess
import kotlinx.cli.*

fun main(args: Array<String>) {
    val parser = ArgParser("forge")
    val arch by parser.option(ArgType.Choice<Architecture>(), shortName="a",
        description = "Architecture to compile for").default(Architecture.Any64)
    val paths by parser.argument(ArgType.String,
        description = "Source file path(s)").vararg()
    parser.parse(args)

    val compiler = Compiler(arch = arch)

    try {
        val translationUnits = compiler.compileScripts(paths)
        translationUnits.forEach { tu ->
            tu.structs.values.forEach { struct ->
                println("struct ${struct.name}(${struct.ctype})")
            }
            tu.functions.values.forEach { fn ->
                println("symbol ${fn.native.cdecl}")
            }
        }
    } catch (compileFailure: CompilationFailure) {
        println("Compilation failed:")
        println(compileFailure.message)
        exitProcess(-1)
    }
}
