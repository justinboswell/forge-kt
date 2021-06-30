package software.amazon.awssdk.forge.compiler

import kotlin.reflect.KClass

class Struct(val kclass: KClass<*>) {
    val name : String = kclass.simpleName!!

    init {

    }
}