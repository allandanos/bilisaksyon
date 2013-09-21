package bilisaksyon

import grails.converters.JSON

class CallbackController {
    
    def callbackService

    /* Sample Request 
    [
    to:68007, 
    utime:1379748683, 
    rrn:420048408976, 
    svc_id:0, 
    text:AKSYON jeje gumana, 
    smsc:strikerS6800in2ngin, 
    from:639193253123, 
    controller:callback
    ]
     */
    def index() { 
        
        // log params
        println params
    
        def response = callbackService.serve(params)
        
        render response as JSON
    }
    
}
