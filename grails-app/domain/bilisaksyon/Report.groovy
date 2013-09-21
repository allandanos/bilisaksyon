package bilisaksyon

class Report {
    
    String hotlineId
    String message
    String rrn
    
    static belongsTo = [reporter: Reporter]

    static constraints = {
        message blank: false
    }
    
    def getReferenceNumber() {
        return id
    }
}
