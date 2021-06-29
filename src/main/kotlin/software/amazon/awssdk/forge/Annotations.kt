
package software.amazon.awssdk.forge

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Resource(val type: KClass<*>)

@Target(AnnotationTarget.CLASS)
annotation class Constructor(val symbol: String)

@Target(AnnotationTarget.CLASS)
annotation class Destructor(val symbol: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Default(val default: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Setter(val symbol: String)

enum class Call {
    STATIC,
    METHOD
}

@Target(AnnotationTarget.FUNCTION)
annotation class Method(val symbol: String, val call: Call)

@Target(AnnotationTarget.FUNCTION)
annotation class Static()
