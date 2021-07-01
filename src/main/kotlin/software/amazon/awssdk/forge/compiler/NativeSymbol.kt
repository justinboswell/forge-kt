package software.amazon.awssdk.forge.compiler

import software.amazon.awssdk.forge.native.NativeType
import software.amazon.awssdk.forge.native.Void
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

abstract class NativeSymbol(val name: String)

class NativeFunction(val function: KCallable<*>)
    : NativeSymbol(function.name) {
    val returnType = function.returnType.nativeType
    val params = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.name!! to it.nativeType }.toList()
    val cdecl: String get() {
        val cparams = this.params.map {
            "${it.second.ctype} ${it.first}"
        }.joinToString(", ")
        return "${returnType.ctype} ${function.name}(${cparams})"
    }
}

class NativeValue(name: String) : NativeSymbol(name)
