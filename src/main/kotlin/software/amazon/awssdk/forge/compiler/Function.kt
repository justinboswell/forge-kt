package software.amazon.awssdk.forge.compiler

import kotlin.reflect.KCallable

open class Function(val scope: TranslationUnit, val function: KCallable<*>) {
    val name = function.name
    val native = NativeFunction(function)
}

class ResourceConstructor(scope: TranslationUnit, function: KCallable<*>, val options: Struct?)
    : Function(scope, function)

class ResourceDestructor(scope: TranslationUnit, function: KCallable<*>)
    : Function(scope, function)
