#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.native.*

external fun aws_crt_event_loop_group_options_new() : Pointer<EventLoopGroupOptions>
external fun aws_crt_event_loop_group_options_release(options: Pointer<EventLoopGroupOptions>)
external fun aws_crt_event_loop_group_options_set_max_threads(options: Pointer<EventLoopGroupOptions>, maxThreads: Int32)

external fun aws_crt_event_loop_group_new() : Pointer<EventLoopGroup>
external fun aws_crt_event_loop_group_release(elg: Pointer<EventLoopGroup>)

@Resource("aws_crt_event_loop_group_options")
@Constructor("aws_crt_event_loop_group_options_new")
@Destructor("aws_crt_event_loop_group_options_release")
interface EventLoopGroupOptions {
    @Default("0")
    @Setter("aws_crt_event_loop_group_options_set_max_threads")
    val maxThreads: Int32
}

@Resource("aws_crt_event_loop_group")
@Constructor("aws_crt_event_loop_group_new", EventLoopGroupOptions::class)
@Destructor("aws_crt_event_loop_group_release")
interface EventLoopGroup
