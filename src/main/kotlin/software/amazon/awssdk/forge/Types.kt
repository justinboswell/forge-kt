package software.amazon.awssdk.forge

interface NativeType {
    val size: Int;
}

open class Int32 : NativeType {
    override val size = 4;
}

open class Int64 : NativeType {
    override val size = 8;
}

open class Char : NativeType {
    override val size = 1;
}

class Float : Int32();

class Double : Int64();

class IntPtr : NativeType {
    override val size: Int
        get() = 4;
}

class Pointer<T : NativeType> : NativeType {
    override val size = IntPtr().size;
}
