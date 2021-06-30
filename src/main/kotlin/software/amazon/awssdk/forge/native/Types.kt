package software.amazon.awssdk.forge.native

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.typeOf

open class NativeType<T>(val size: kotlin.Int, open val ctype: String, val default: T)

abstract class Int<T>(size: kotlin.Int, ctype: String)
    : NativeType<kotlin.Int>(size, ctype, 0)

class Bool() : NativeType<kotlin.Boolean>(1, "_Bool", false)

class Int32() : Int<kotlin.Int>(4, "int32_t")
class UInt32() : Int<kotlin.Int>(4, "uint32_t")

class Int64() : Int<kotlin.Long>(8, "int64_t")
class UInt64() : Int<kotlin.Long>(8, "uint64_t")

class Uint8() : Int<kotlin.Char>(1, "uint8_t")
class Char() : Int<kotlin.Char>(1, "char")

class Float()
    : NativeType<kotlin.Float>(4, "float", 0.0f)

class Double()
    : NativeType<kotlin.Double>(8, "double", 0.0)

class Void()
    : NativeType<java.lang.Void?>(0, "void", null)

@OptIn(ExperimentalStdlibApi::class)
class CString()
    : NativeType<kotlin.String>(Pointer<Char>(typeOf<Pointer<Char>>()).size, "char*", "")

class Pointer<in T : Any>(private val pointerType: KType)
    : NativeType<kotlin.Long>(8, "void*", 0) {

    val kclass = this.pointerType.toPointeeKClass()
    val ktype = kclass.simpleName + "?"

    val pointeeType = pointerType.toPointeeKType()
    override val ctype = resolveCtype()

    private fun resolveCtype() : String {
        // Special case resource tagged types
        val resource = kclass.annotations.find {
            it.annotationClass == Resource::class
        }
        if (resource != null) {
            return (resource as Resource).ctype + "*"
        }
        // use the ctype from the native type
        return ((pointeeType.classifier as KClass<*>).constructors.first().call() as NativeType<*>).ctype + "*"
    }

    private fun KType.toPointeeKType() : KType {
        assert(this.arguments.size == 1)
        return this.arguments[0].type!!
    }

    private fun KType.toPointeeKClass() : KClass<*> {
        return toPointeeKType().classifier as KClass<*>
    }
}
