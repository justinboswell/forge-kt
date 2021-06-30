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
    val returnType = function.returnType.toNativeType()
    val params = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.name!! to it.toNativeType() }.toList()
    val cdecl: String get() {
        val cparams = this.params.map {
            "${it.second.ctype} ${it.first}"
        }.joinToString(",")
        return "${returnType.ctype} ${function.name}(${cparams})"
    }

    fun KType.toNativeType() : NativeType<*> {
        @Suppress("UNCHECKED_CAST")
        val kclass = this.classifier as KClass<NativeType<*>>
        // Special case Pointer<T>
        if (kclass.simpleName == "Pointer") {
            val ctor = kclass.constructors.first {
                !it.parameters.isEmpty()
            }
            return ctor.call(this)
        }
        // Convert Kotlin Unit -> Void
        if (this.classifier == kotlin.Unit::class) {
            return Void()
        }

        // Should be a primitive type
        try {
            val ctor = kclass.constructors.first()
            return ctor.call()
        } catch (ex: NoSuchElementException) {
            println("ERROR: Type $kclass is not a recognized native type");
            return Void()
        }
    }

    fun KParameter.toNativeType() : NativeType<*> {
        return this.type.toNativeType()
    }
}

class NativeValue(name: String) : NativeSymbol(name)
