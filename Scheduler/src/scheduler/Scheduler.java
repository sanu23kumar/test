package scheduler;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Scheduler {

    public static void main(String[] args) {
        
        DBHandling db = new DBHandling();
        
//        db.newUser(1, "reese", "reesejohn", "reese");
//        db.newUser(4, "sanu", "kumar2010", "sanu");
//        db.deleteUser(1);
//        db.addASubject(1, 5, "E",3, "3");
//        db.DeleteASubject(4, 2, "Maths");
        db.scheduleSubjectsTime(1);

        try {
            
            db.st.close();
            db.co.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
}
