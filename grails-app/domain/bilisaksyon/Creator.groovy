package bilisaksyon

class Creator {

    String mobile
    
    Date dateCreated
    Date lastUpdated
    
    static hasMany = [hotlines : Hotline]
    
    static constraints = {
        mobile unique: true
    }
}
