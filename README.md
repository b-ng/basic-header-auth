# basic-header-auth
_springboot app, that implements a basic header check._

This application implements a simple solution to verifying a basic header before allowing a client access to the rest api.
In this case a `HandlerInterceptorAdapter` is used.

In this case the Interceptor is used because the api is intentionally stateless.
