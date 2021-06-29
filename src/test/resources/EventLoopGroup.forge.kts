#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.*

@Resource
@Constructor("aws_crt_event_loop_group_new")
@Destructor("aws_crt_event_loop_group_release")
interface EventLoopGroup {

}
