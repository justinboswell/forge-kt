
package software.amazon.awssdk.forge.native

/// Specifies that this class represents a managed native resource
@Target(AnnotationTarget.CLASS)
annotation class Resource(val ctype: String)

/// Specifies the constructor for the resource represented by this class. Implies static calling convention
@Target(AnnotationTarget.CLASS)
annotation class Constructor(val symbol: String)

/// Specifies the destructor for the resource represented by this class. Implies method calling convention
@Target(AnnotationTarget.CLASS)
annotation class Destructor(val symbol: String)

/// Provides a default value for the property. While provided as a string, will be converted to the correct type
@Target(AnnotationTarget.PROPERTY)
annotation class Default(val default: String)

enum class Call {
    STATIC, /// Static call, no automatic additional arguments
    METHOD /// Method call, automatically insert the native resource/opaque handle associated with the containing class as the first argument
}

/// Indicates the native symbol to bind to, and the calling convention
@Target(AnnotationTarget.FUNCTION)
annotation class Method(val symbol: String, val call: Call)

/// Indicates the native symbol to bind to as a setter, implies a method calling convention
@Target(AnnotationTarget.PROPERTY)
annotation class Setter(val symbol: String)

/// Indicates the native symbol to bind to as a getter, implies a method calling convention
@Target(AnnotationTarget.PROPERTY)
annotation class Getter(val symbol: String)

/// Indicates that the function is intended to be a static function when generated
@Target(AnnotationTarget.FUNCTION)
annotation class Static
