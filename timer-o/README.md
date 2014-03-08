vert-zeromq
===========

![Build status](https://travis-ci.org/p14n/vert-zeromq.png)

Providing a bridge from zero-mq to the vert-x event bus.

Available in the module registry as p14n~vert-zeromq~0.0.1.  Note this release is not production ready.  1.0.0 will be
released with vert.x 2.1 final, and will be production ready.

This module enables you to remotely call a handler on the bus, receive replies, and reply back.  It also allows you
to register a 0mq socket as a handler, receive calls to that handler address, and reply back.


If a reply handler was supplied by the sender of the message received at the socket,
that handler's address is included as the second frame of the message.

NOTE - you cannot currently reply to a 0mq socket that has registered as a handler (although it can reply to you).

Config requires the address the module should listen on:

```json
{
 "address":"tcp://*:5558"
}
```
