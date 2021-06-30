package software.amazon.awssdk.forge.compiler

import software.amazon.awssdk.forge.native.NativeType
import software.amazon.awssdk.forge.native.Resource
import software.amazon.awssdk.forge.native.Void
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

inline fun <reified T: Any> KClass<*>.getAnnotation() : T? {
    return annotations.find {
        it.annotationClass == T::class
    } as T
}

fun KClass<*>.hasNativeResource(): Boolean {
    return getAnnotation<Resource>() != null
}

fun KClass<*>.nativeResourceCtype() : String {
    val resource = getAnnotation<Resource>()
        ?: throw UnsupportedOperationException("class $this does not have an associated native resource. Is a @Resource annotation missing?")
    return resource.ctype
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
