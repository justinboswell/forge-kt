package software.amazon.awssdk.forge.compiler

import software.amazon.awssdk.forge.native.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.memberFunctions

inline fun <reified T: Any> KClass<*>.getAnnotation(recursive : Boolean = true) : T? {
    val classesToSearch = listOf(this).toMutableList()
    if (recursive) {
        classesToSearch += allSuperclasses.filter {
            it != Any::class
        }
    }

    return classesToSearch.firstNotNullOfOrNull { sc ->
        sc.annotations.find {
            it.annotationClass == T::class
        }
    } as T?
}

val KClass<*>.hasNativeResource: Boolean get() {
    return getAnnotation<Resource>() != null
}

val KClass<*>.nativeResourceCtype : String? get() {
    val resource = getAnnotation<Resource>()
        ?: return null
    return resource.ctype
}

val KClass<*>.hasNativeConstructor: Boolean get() {
    return getAnnotation<Constructor>() != null
}

val KClass<*>.nativeConstructor: Constructor get() {
    return getAnnotation()
        ?: throw CompilationFailure("class $this does not have an associated native constructor. Is a @Constructor annotation missing?")
}

val KClass<*>.hasNativeDestructor: Boolean get() {
    return getAnnotation<Destructor>() != null
}

val KClass<*>.nativeDestructor: Destructor get() {
    return getAnnotation()
        ?: throw CompilationFailure("class $this does not have an associated native destructor. Is a @Destructor annotation missing?")
}

private val objectFunctions = setOf("equals", "hashCode", "toString")
val KClass<*>.localMethods: Collection<KFunction<*>> get() {
    return memberFunctions.filter {
        !objectFunctions.contains(it.name)
    }
}

val KType.nativeType : NativeType<*> get() {
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
        throw CompilationFailure("ERROR: $kclass is not a recognized native type")
    }
}

val KParameter.nativeType : NativeType<*> get() {
    return this.type.nativeType
}
