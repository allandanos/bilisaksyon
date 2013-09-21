package bilisaksyon

import grails.converters.JSON

class ApiController {

    def index() { 
    
        def response = [
            status: "ok",
            message: "success"
        ]
        
        render response as JSON
    }
}
