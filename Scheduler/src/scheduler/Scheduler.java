package scheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Scheduler {

    public static void main(String[] args) {
        
        DBHandling db = new DBHandling();
        
//        db.newUser(1, "reese", "reesejohn", "reese");
//        db.newUser(4, "sanu", "kumar2010", "sanu");
//        db.deleteUser(4);
//        db.addASubject(1, 1, "Maths",0, "3");
//        db.DeleteASubject(4, 2, "Maths");
        try {
            db.scheduleSubjectsTime(1);
        } catch (Exception ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
//         
           try{
               db.st.close();
               db.co.close();
           } catch (Exception e){
               Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, e);
           }
            
    }
    
}
