# Loader -> Read handlers from disk
# js-runtime/Inject -> load handlers into a Context :
## Either load them as functions or load them as a module?
### Loading them as string: eval the entrie content of the file as string into the context.


* config file: Read the config file and bootstrap the server json or edn? (with chesire we can have both) exylerate.edn/exylerate.json
* UI: Recommend way of configuring 

We use an engine for every context and use a Source object to compile our handlers and libs --> cached into the engine --> performant
```javascript
$__exylerate.handlers['HTTP/users/home'] = {
    (function () {
            $FILE_CONTENT;
            return {GET: GET(),
                    POST: POST(),
                    }
    })();
}
```
$__exylerate.handlers['HTTP/users/home'].get()

$FILE_CONTENT = "function GET () {}; function POST () {};"

--> Load handlers into runtime

Read from 


[source files]
1) read from edn file
2) compute files based on path
    |--> src/http/user/home/
    |--> src/http/user/[$user.js]/
    |--> src/database/

    :> convert path routes to reitit paths


====================================================
   * function create Source -> Read files -> create javascript source string  and compile into Source
   * function create Context with source

    When server starts:
        * load config file
        * init runtime
        * set up reitit handlers to call a runtime-> create-context -> call the corosponding JS handelr -> close context when request is finished
