package software.amazon.awssdk.forge.compiler

import software.amazon.awssdk.forge.native.Resource
import kotlin.reflect.KClass

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
