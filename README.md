# basic-header-auth
_springboot app, that implements a basic header check._

This application implements a simple solution to verifying a basic header before allowing a client access to the rest api.
In this case a `HandlerInterceptorAdapter` is used.

In this case the Interceptor is used because the api is intentionally stateless.


### `Api.class`
This is the REST controller.

### `BasicHeaderInterceptor.class`
This is the header validation that runs before an endpoint's method is hit. (returns true to continue, false to abort.)
> _It is important to note here, that we write to the response object before returning false to set the response that the user will receive._


> Also, `parseClientCredentials()` could use the `request.getHeader()` to avoid iterating through a while loop, however, using the `request.getHeaders()` allows you to support multiple Authorization headers. _(eg: a Basic client token, and an oauth Bearer token.)_ Since`request.getHeader()` just grabs the first header that matches (no guaranteed order).

### `Properties.class`
Here we read the clientId and clientSecret from a properties file. So that the values are configurable.

### `WebConfig.class`
Here we wire our `BasicHeaderInterceptor` into spring's existing workflow.
