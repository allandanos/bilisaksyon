package bilisaksyon

class Hotline {
    
    String name
    
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [creator: Creator]
    static hasMany = [subscribers: Subscriber]

    static constraints = {
        
    }
}
