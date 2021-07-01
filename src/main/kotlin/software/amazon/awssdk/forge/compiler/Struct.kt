package software.amazon.awssdk.forge.compiler

import kotlin.reflect.KClass

class Struct(val scope: TranslationUnit, private val kclass: KClass<*>) {
    val name : String = kclass.simpleName!!
    val ctype = kclass.nativeResourceCtype
    val constructor = resolveConstructor()
    val destructor = resolveDestructor()
    val methods = kclass.localMethods.map { Function(scope, it) }

    private fun resolveConstructor() : ResourceConstructor? {
        if (!kclass.hasNativeConstructor) {
            return null
        }
        val ctorDesc = kclass.nativeConstructor
        val ctorFunction = scope.functions[ctorDesc.symbol] ?: throw CompilationFailure("Unable to resolve symbol ${ctorDesc.symbol}")
        return ResourceConstructor(scope, ctorFunction.function)
    }

    private fun resolveDestructor() : ResourceDestructor? {
        if (!kclass.hasNativeDestructor) {
            return null
        }

        val dtorDesc = kclass.nativeDestructor
        val dtorFunction = scope.functions[dtorDesc.symbol] ?: throw CompilationFailure("Unable to resolve symbol ${dtorDesc.symbol}")
        return ResourceDestructor(scope, dtorFunction.function)
    }
}