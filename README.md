Demonstration of MDC propagation in a Play app.

The solution I'm attempting is to override the `executionContext` in `MyApplicationLoader`. 

logback is configured to include the MDC. The `HttpContextFilter` adds a requestId to the MDC. The `AccessLogFilter` is the last user code that runs on each request,
 and if everything works correctly, the requestId will be presented in its log statement.
 
Currently, the `/` (which uses play-ws) is not correctly propagating the MDC.

The '/sleep' route does work correctly. 

Examplelogs from requesting http://localhost:9000 that demonstrates the failure
```
2018-01-15 11:12:55,406 [info] [application-akka.actor.default-dispatcher-2] [] a.e.s.Slf4jLogger:89 Slf4jLogger started
2018-01-15 11:12:55,646 [info] [play-dev-mode-akka.actor.default-dispatcher-8] [] p.a.Play:128 Application started (Dev)
2018-01-15 11:12:55,729 [info] [play-dev-mode-akka.actor.default-dispatcher-8] [RequestId=ilZgFVuyShW+q65SK9O3ag] f.HttpContextFilter:30 MDC set
2018-01-15 11:12:55,739 [info] [play-dev-mode-akka.actor.default-dispatcher-8] [RequestId=ilZgFVuyShW+q65SK9O3ag] c.HomeController:12 HomeController.index
2018-01-15 11:12:56,366 [info] [AsyncHttpClient-2-2] [] u.MdcExecutionContext:11 Current mdc: null
2018-01-15 11:12:56,366 [info] [AsyncHttpClient-2-1] [] u.MdcExecutionContext:11 Current mdc: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:15 oldMDCContext: {}
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:15 oldMDCContext: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:17 Running runnable with mdc: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:17 Running runnable with mdc: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:11 Current mdc: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:21 Reverting to oldMDCContext: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:21 Reverting to oldMDCContext: {}
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:15 oldMDCContext: null
2018-01-15 11:12:56,368 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:17 Running runnable with mdc: null
2018-01-15 11:12:56,369 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:11 Current mdc: null
2018-01-15 11:12:56,374 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:21 Reverting to oldMDCContext: null
2018-01-15 11:12:56,374 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:15 oldMDCContext: {}
2018-01-15 11:12:56,374 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:17 Running runnable with mdc: null
2018-01-15 11:12:56,380 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:11 Current mdc: null
2018-01-15 11:12:56,381 [info] [application-akka.actor.default-dispatcher-2] [] u.MdcExecutionContext:21 Reverting to oldMDCContext: {}
2018-01-15 11:12:56,381 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:15 oldMDCContext: null
2018-01-15 11:12:56,381 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:17 Running runnable with mdc: null
2018-01-15 11:12:56,386 [info] [application-akka.actor.default-dispatcher-5] [] f.AccessLogFilter:41 0:0:0:0:0:0:0:1 Content-Type=none/none, Remote-Address=0:0:0:0:0:0:0:1:61766, Raw-Request-URI=/, Tls-Session-Info=[Session-1, SSL_NULL_WITH_NULL_NULL], Host=localhost:9000, Connection=keep-alive, User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36, Upgrade-Insecure-Requests=1, Accept=text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, image/apng, */*;q=0.8, Accept-Encoding=gzip, deflate, br, Accept-Language=en-US, en;q=0.9, Cookie=grafana_sess=f224e8413c45f426, Timeout-Access=<function1> GET / requestTime=656 status=200
2018-01-15 11:12:56,387 [error] [application-akka.actor.default-dispatcher-5] [] f.AccessLogFilter:43 No MDC
2018-01-15 11:12:56,415 [info] [application-akka.actor.default-dispatcher-5] [] u.MdcExecutionContext:21 Reverting to oldMDCContext: null
```
