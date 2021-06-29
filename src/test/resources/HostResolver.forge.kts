#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.*

@Resource(HostResolver::class)
@Destructor("aws_crt_host_resolver_release")
interface HostResolver {

}

@Constructor("aws_crt_host_resolver_default_new")
interface DefaultHostResolver : HostResolver {

}
