package software.amazon.awssdk.forge.compiler

class Context {
    val structs = mutableMapOf<String, Struct>()
    val symbols = mutableMapOf<String, NativeSymbol>()
}


