package bilisaksyon

class Report {
    
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
