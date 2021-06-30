package software.amazon.awssdk.forge.compiler

class CompilationFailure(message: String, cause: Throwable? = null) : RuntimeException(message, cause)