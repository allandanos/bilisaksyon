package bilisaksyon

class Subscriber {
    
    String mobile
    
    Date dateCreated
    Date lastUpdated
    
    static hasMany = [hotlines: Hotline]
    static belongsTo = Hotline

    static constraints = {
        
    }
}
