package bilisaksyon

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType

class CallbackService {

    def serve(params) {
        
        def to = params.to
        def utime = params.utime
        def rrn = params.rrn
        def svc_id = params.svc_id
        def text = params.text
        def smsc = params.smsc
        def from = params.from
        
        if (text.contains("AKSYON CREATE")) {
            
            create(params)
            
        } else if (text.contains("AKSYON REMOVE")) {
            
            remove(params)
            
        } else if (text.contains("AKSYON LIST")) {
            
            list(params)
            
        } else if (text.contains("AKSYON ON")) {
            
            on(params)
            
        } else if (text.contains("AKSYON OFF")) {
            
            off(params)
            
        } else if (text.contains("AKSYON HELP")) {
            
            help(params)
            
        } else {
            
        }
        
        def msg = [
            text: "You sent: $text",
            to: to,
            rrn: rrn,
            msisdn: from
        ]
        
        //println sendMessage(msg)
        
        return [ status: "ok",  message: "success" ]
    }
    
    // create hotline
    def create(params) {
        
        Creator.find
        
    }
    
    // remove hotline
    def remove(params) {
        
    }
    
    // list all hotlines
    def list(params) {
        
        def text = ""
        def hotlines = Hotline.list()
       
        hotlines.each { it ->
            text.append(it)
        }
        
        def msg = [
            text: text,
            to: to,
            rrn: rrn,
            msisdn: from
        ]
        
        println sendMessage(msg)
    }
    
    // turn subscription on
    def on(params) {
        
    }
    
    // turn subscription off
    def off(params) {
        
    }
    
    // incident report from reporter
    def report(params) {
        
    }
    
    // broadcast to subscribers
    def broadcast(params) {
        
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
