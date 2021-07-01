package software.amazon.awssdk.forge.compiler

import kotlin.reflect.KClass

class TranslationUnit(scriptClass: KClass<*>) {
    class Script(scriptClass: KClass<*>) {
        val classes = scriptClass.nestedClasses.associateBy { it.simpleName!! }
        val callables = scriptClass.localMethods.associateBy { it.name }
    }

    private val script = Script(scriptClass)
    val structs = mutableMapOf<String, Struct>()
    val functions = mutableMapOf<String, Function>()

    init {
        // Convert from Kotlin reflection to our representation
        functions += script.callables.map {
            it.key to Function(this, it.value)
        }
        structs += script.classes.map {
            it.key to Struct(this, it.value)
        }
    }
}


