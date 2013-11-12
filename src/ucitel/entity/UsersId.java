package ucitel.entity;
// Generated 29.6.2011 14:38:58 by Hibernate Tools 3.2.1.GA



/**
 * UsersId generated by hbm2java
 */
public class UsersId  implements java.io.Serializable {


     private int userId;
     private String username;

    public UsersId() {
    }

    public UsersId(int userId, String username) {
       this.userId = userId;
       this.username = username;
    }
   
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof UsersId) ) return false;
		 UsersId castOther = ( UsersId ) other; 
         
		 return (this.getUserId()==castOther.getUserId())
 && ( (this.getUsername()==castOther.getUsername()) || ( this.getUsername()!=null && castOther.getUsername()!=null && this.getUsername().equals(castOther.getUsername()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getUserId();
         result = 37 * result + ( getUsername() == null ? 0 : this.getUsername().hashCode() );
         return result;
   }   


}


