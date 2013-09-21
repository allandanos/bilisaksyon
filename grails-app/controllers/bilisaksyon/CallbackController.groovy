package bilisaksyon

import grails.converters.JSON

class CallbackController {

    def index() { 
        
        println params
    
        def response = [
            status: "ok",
            message: "success"
        ]
        
        render response as JSON
    }
}
