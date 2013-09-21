package bilisaksyon

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType

class CallbackService {

    def serve(params) {
        
        
        def response
        
        def textMsg = params?.text?.toString()
        def  msgContent = textMsg?.trim()?.toUpperCase();
        
		if (msgContent.startsWith("AKSYON CREATE ")) {
			String hotlineId = msgContent.substring(14); 
			println("Create: "+hotlineId);
            
            response = create(params, hotlineId)
			
		} else if (msgContent.startsWith("AKSYON REMOVE ")) {
			String hotlineId = msgContent.substring(14); 
			println("Remove: "+hotlineId);
            
            response = remove(params, hotlineId)
			
		} else if (msgContent.startsWith("AKSYON ON ")) {
			String hotlineId = msgContent.substring(10); 
			println("On: "+hotlineId);
            
            response = on(params, hotlineId)
			
		} else if (msgContent.startsWith("AKSYON OFF ")) {
			String hotlineId = msgContent.substring(11); 
			println("Off: "+hotlineId);
            
            response = off(params, hotlineId)
			
		} else if (msgContent.equals("AKSYON HELP")) {
			println("Help all triggered.");
			
		} else if (msgContent.equals("AKSYON HELP CREATE")) {
			println("Help create triggered.");
			
		} else if (msgContent.equals("AKSYON HELP REMOVE")) {
			println("Help remove triggered.");
			
		} else if (msgContent.equals("AKSYON HELP ON")) {
			println("Help on triggered.");
			
		} else if (msgContent.equals("AKSYON HELP OFF")) {
			println("Help off triggered.");
			
		} else if (msgContent.startsWith("AKSYON ")) {
			String msg = textMsg.trim().substring(7);
			int index = msg.indexOf(" ");
			String hotlineId = msg.substring(0, index);
			msg = msg.substring(index+1);
			println("Hotline id: "+hotlineId+" Report: "+msg);
            
            response = report(params, hotlineId, msg)
			
		}
       
        
        return response
    }
    
    // create hotline
    def create(params, code) {
        
        def status = "ERROR"
        
        def msg = [
            to: params.to,
            rrn: params.rrn,
            msisdn: params.from
        ]
        
        def creator = Creator.findByMobile(params.from)
        if (!creator) {
            creator = new Creator(mobile: params.from)
        }
        
        def hotline = Hotline.findByCode(code)
        if (hotline) {

            msg.text = "Hotline $code exists"
            
        } else {
            
            hotline = new Hotline(code: code)
            creator.addToHotlines(hotline)
            creator.save(flush: true)

            msg.text = "Hotline $code created successfully"
            status = "OK"
            
        }
        
        println msg.text
        sendMessage(msg)
        return [status: status, message: msg.text]
    }
    
    // remove hotline
    def remove(params, code) {
        
        def status = "ERROR"
        def msg = [
            to: params.to,
            rrn: params.rrn,
            msisdn: params.from
        ]
        
        def creator = Creator.findByMobile(params.from)
        if (!creator) {
            msg.text = "You're not the creator of Hotline $code"
        } else {
            def hotline = Hotline.findByCode(code)
            if (hotline) {
                
                if (creator.hotlines?.contains(hotline)) {
                    
                    creator.removeFromHotlines(hotline)
                    //creator.save(flush: true)
                    hotline.delete(flush: true)
                    
                    msg.text = "Hotline $code removed successfully"
                    status = "OK"
                    println msg.text
                    sendMessage(msg)
                } else {
                    msg.text = "You're not the creator of Hotline $code"
                }
                    
            } else {
                msg.text = "Hotline $code does not exist"
                
            }
        }
        
        println msg.text
        sendMessage(msg)
        return [status: status, message: msg.text]
        
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
    def on(params, code) {
        
        def status = "ERROR"
        
        def msg = [
            to: params.to,
            rrn: params.rrn,
            msisdn: params.from
        ]
        
        def subscriber = Subscriber.findByMobile(params.from)
        if (!subscriber) {
            subscriber = new Subscriber(mobile: params.from)
        }
        
        def hotline = Hotline.findByCode(code)
        if (hotline) {

            subscriber.addToHotlines(hotline)
            subscriber.save(flush: true)

            msg.text = "Successfully subscribe to Hotline $code. You will now receive incident reports from this Hotline."
            status = "OK"
            
        } else {
            msg.text = "Hotline $code does not exist"
        }
        
        println msg.text
        sendMessage(msg)
        return [status: status, message: msg.text]
        
    }
    
    // turn subscription off
    def off(params, code) {
        
        def status = "ERROR"
        
        def msg = [
            to: params.to,
            rrn: params.rrn,
            msisdn: params.from
        ]
        
        def subscriber = Subscriber.findByMobile(params.from)
        if (subscriber) {
            def hotline = Hotline.findByCode(code)
            if (hotline) {

                subscriber.removeFromHotlines(hotline)
                subscriber.save(flush: true)

                msg.text = "Successfully ubsubscribe to Hotline $code. You will no longer receive incident reports from this Hotline."
                status = "OK"
            
            } else {
                msg.text = "Hotline $code does not exist"
            }
        } else {
            msg.text = "You're not subscribing to any hotlines."
        }
        
        
        
        println msg.text
        sendMessage(msg)
        return [status: status, message: msg.text]
        
    }
    
    // incident report from reporter
    def report(params, code, message) {
        
        def status = "ERROR"
        
        def msg = [
            to: params.to,
            rrn: params.rrn,
            msisdn: params.from
        ]
        
        def report = new Report(
            hotline: code,
            message: message,
            rrn: params.rrn
        )
        
        def reporter = Reporter.findByMobile(params.from)
        if (!reporter) {
            reporter = new Reporter(mobile: params.from)
        }
        
        reporter.addToReports(report)
        reporter.save(flush: true)
        
        // broadcast
        def hotline = Hotline.findByCode(code)
        if (hotline) {

            msg.text = "Your concern has been reported to $code"
            status = "OK"
            
            hotline.subscribers?.each { it -> 
                broadcast(params, it, code, message)
            }
            
        } else {
            msg.text = "Hotline $code does not exist"
        }
        
        println msg.text
        sendMessage(msg)
        return [status: status, message: msg.text]
        
    }
    
    // broadcast to subscribers
    def broadcast(params, subscriber, code, message) {
     
        def msg = [
            text: "$code: $message",
            to: params.to,
            rrn: params.rrn,
            msisdn: subscriber.mobile
        ]
        
        println msg.text
        sendMessage(msg)
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
