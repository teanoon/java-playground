```js
{
    traceId: String;
    spans: [
        {
            spanId: Integer;
            timestamp: Long;
            Client Sent/Server Received/Server Sent/Client Received
            spans: [...]
        },
        { ... },
        { ... },
        { ... }
    ]
}
```
