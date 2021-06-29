
package software.amazon.awssdk.forge

@Target(AnnotationTarget.CLASS)
annotation class Resource

@Target(AnnotationTarget.CLASS)
annotation class Constructor(val symbol: String)

@Target(AnnotationTarget.CLASS)
annotation class Destructor(val symbol: String)
