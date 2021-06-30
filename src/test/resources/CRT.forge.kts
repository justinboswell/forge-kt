
import software.amazon.awssdk.forge.native.*

external fun aws_crt_last_error(): Int32
external fun aws_crt_error_str(errorCode: Int32) : CString
external fun aws_crt_error_name(errorCode: Int32) : CString
external fun aws_crt_error_debug_str(errorCode: Int32): CString
external fun aws_crt_reset_error()

interface CRT {
    @Static
    @Method("aws_crt_last_error", Call.STATIC)
    fun lastError(): Int32

    @Static
    @Method("aws_crt_error_str", Call.STATIC)
    fun errorString(errorCode: Int32): CString

    @Static
    @Method("aws_crt_error_name", Call.STATIC)
    fun errorName(errorCode: Int32): CString

    @Static
    @Method("aws_crt_error_debug_str", Call.STATIC)
    fun errorDebugString(errorCode: Int32): CString

    @Static
    @Method("aws_crt_reset_error", Call.STATIC)
    fun resetLastError()
}
