package ucitel.entity;
// Generated 29.6.2011 14:38:58 by Hibernate Tools 3.2.1.GA



/**
 * Themes generated by hbm2java
 */
public class Themes  implements java.io.Serializable {


     private Integer themeId;
     private String theme;
     private int userId;
     private String language;

    public Themes() {
    }

	
    public Themes(String theme, int userId) {
        this.theme = theme;
        this.userId = userId;
    }
    public Themes(String theme, int userId, String language) {
       this.theme = theme;
       this.userId = userId;
       this.language = language;
    }
   
    public Integer getThemeId() {
        return this.themeId;
    }
    
    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }
    public String getTheme() {
        return this.theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getLanguage() {
        return this.language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }




}


