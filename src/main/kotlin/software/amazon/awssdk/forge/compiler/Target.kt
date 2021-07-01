package software.amazon.awssdk.forge.compiler

enum class OS(val oses: Int) {
    UNIX(1),
    MACOS(2),
    WINDOWS(4)
}

enum class Architecture(val bits: Int) {
    x86(32),
    x86_64(64),
    arm32(32),
    arm64(64),

    Any32(32),
    Any64(64),
}

enum class Target(val os: OS, val arch: Architecture)