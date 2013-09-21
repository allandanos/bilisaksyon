package bilisaksyon

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType

import grails.converters.JSON

class CallbackController {

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
        
        def to = params.to
        def utime = params.utime
        def rrn = params.rrn
        def svc_id = params.svc_id
        def text = params.text
        def smsc = params.smsc
        def from = params.from
    
        def response = [
            status: "ok",
            message: "success"
        ]
        
        def msg = [
            text: "You sent: $text",
            to: to,
            rrn: rrn,
            msisdn: from
        ]
        
        println sendMessage(msg)
        
        render response as JSON
    }
    
    /* Sample request
    username=YourUsername
    password=YourPassword
    to=68007
    msisdn=MobileNumber
    text=ReplyMessage
    rrn=RRN
    binfo=BINFO
    on=BOOLEAN
    dlrurl=DLR 
    service=aksyon
    kw=
     */
    def sendMessage(params) {
        def message = URLEncoder.encode(params.text, "ISO-8859-1")
        def url = "http://121.58.235.156/sms/sms_out.php?username=allandanos&password=kulotzky69&to=${params.to}&msisdn=${params.msisdn}&text=${message}&rrn=${params.rrn}&binfo=0000&service=aksyon&kw="
    
        def http = new HTTPBuilder(url)
        
        http.request(Method.POST, ContentType.TEXT) { req ->

            response.success = { resp, reader ->
                println resp.statusLine
                def BufferedReader r = new BufferedReader(reader)
                def lines = r.readLines()
                def str = new StringBuilder()
                lines.each { it ->
                    str.append(it)
                }
                
				return "SENT: ${str.toString()}"
			}
             
            response.failure = { resp ->
                log.warn "NOT SENT: Error occured during sending message"
				return null
			}
        }
    }
    
}
