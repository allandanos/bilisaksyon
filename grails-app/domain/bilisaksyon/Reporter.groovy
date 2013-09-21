package bilisaksyon

class Reporter {
    
    String mobile
    
    Date dateCreated
    Date lastUpdated
    
    static hasMany = [reports: Report]

    static constraints = {
        mobile unique: true
    }
}
