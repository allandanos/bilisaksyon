package bilisaksyon

class Hotline {
    
    String code
    
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [creator: Creator]
    static hasMany = [subscribers: Subscriber]

    static constraints = {
        mobile code: true
    }
}
