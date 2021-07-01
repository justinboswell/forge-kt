#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.native.*

external fun aws_crt_host_resolver_default_new() : Pointer<HostResolver>
external fun aws_crt_host_resolver_release(resolver: Pointer<HostResolver>)

@Resource("aws_crt_host_resolver")
@Destructor("aws_crt_host_resolver_release")
interface HostResolver

@Constructor("aws_crt_host_resolver_default_new")
interface DefaultHostResolver : HostResolver
