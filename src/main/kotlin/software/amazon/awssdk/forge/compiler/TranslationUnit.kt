package software.amazon.awssdk.forge.compiler

class TranslationUnit {
    val structs = mutableMapOf<String, Struct>()
    val symbols = mutableMapOf<String, NativeSymbol>()
}


