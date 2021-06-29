package software.amazon.awssdk.forge

open class NativeType<T>(val size: Int, val default: T)

class Int32(default: Int = 0)
    : NativeType<kotlin.Int>(4, default)

class Int64(default: Long = 0)
    : NativeType<kotlin.Long>(8, default)

class Char(default: kotlin.Char = 0.toChar())
    : NativeType<kotlin.Char>(1, default)

class Float(default: kotlin.Float = 0.0f)
    : NativeType<kotlin.Float>(4, default)

class Double(default: kotlin.Double = 0.0)
    : NativeType<kotlin.Double>(8, default)

open class IntPtr(default: kotlin.Long = 0)
    : NativeType<kotlin.Long>(8, default)

class Void()
    : NativeType<java.lang.Void?>(0, null)

class Pointer<T> : IntPtr()
