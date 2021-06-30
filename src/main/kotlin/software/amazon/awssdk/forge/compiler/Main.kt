package software.amazon.awssdk.forge.compiler


import kotlin.system.exitProcess


fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("Usage: forge <PATH>")
    }

    val compiler = Compiler()

    val paths = args.toList()
    try {
        val translationUnits = compiler.compileSources(paths)
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
