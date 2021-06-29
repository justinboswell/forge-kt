#!/usr/bin/env kotlin

import software.amazon.awssdk.forge.*

external fun aws_crt_event_loop_group_options_new() : Pointer<EventLoopGroupOptions>
external fun aws_crt_event_loop_group_options_release(options: Pointer<EventLoopGroupOptions>)
external fun aws_crt_event_loop_group_options_set_num_threads(options: Pointer<EventLoopGroupOptions>, numThreads: Int32)

external fun aws_crt_event_loop_group_new()
external fun aws_crt_event_loop_group_release(elg: Pointer<EventLoopGroup>)

@Resource(EventLoopGroupOptions::class)
@Constructor("aws_crt_event_loop_group_options_new")
@Destructor("aws_crt_event_loop_group_options_release")
interface EventLoopGroupOptions {
    @Default("0")
    @Setter("aws_crt_event_loop_group_options_set_num_threads")
    val numThreads: Int32
}

@Resource(EventLoopGroup::class)
@Constructor("aws_crt_event_loop_group_new")
@Destructor("aws_crt_event_loop_group_release")
interface EventLoopGroup
