#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.native.*

external fun aws_crt_default_allocator() : Pointer<Allocator>
external fun aws_crt_mem_acquire(size: Int64) : Pointer<Void>
external fun aws_crt_mem_calloc(num_elements: Int64, element_size: Int64) : Pointer<Void>
external fun aws_crt_mem_release(mem: Pointer<Void>)

@Resource("aws_crt_allocator")
interface Allocator {
    @Method("aws_crt_mem_acquire", Call.STATIC)
    fun acquire(size: Int64): Pointer<Void>

    @Method("aws_crt_mem_release", Call.STATIC)
    fun release(mem: Pointer<Void>)

    @Method("aws_crt_mem_calloc", Call.STATIC)
    fun calloc(num_elements: Int64, element_size: Int64) : Pointer<Void>

    @Static
    @Method("aws_crt_default_allocator", Call.STATIC)
    fun default() : Pointer<Allocator>
}
