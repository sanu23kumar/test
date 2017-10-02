package scheduler;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandling {

    Statement st;
    Connection co;
    ResultSet rs = null;
    
    DBHandling(){
        System.out.println("I'm here");
        try {
            //For Windows, use
            //Class.forName("com.mysql.jdbc.Driver");
            //co=DriverManager.getConnection("jdbc:mysql://localhost:3306/yeah","root","");
            co = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+"sanu"+"?autoReconnect=true&useSSL=false", "root", "kumar2010");//For mac
            st = co.createStatement();
            
            st.execute("create table if not exists user(id int not null primary key auto_increment, username varchar(20) not null,  password varchar(100) not null, name varchar(20) not null)");
          
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    void addASubject(int id, int S_ID, String S_Name, int S_Priority, String S_Time, String td){
        
        try 
        {

            st.executeUpdate("insert into " + id + "_UserSubjects values(" + S_ID + ",'" + S_Name + "'," + S_Priority + ",'" + td + "')");
            st.execute("ALTER TABLE "+ id +"_TimeSchedule ADD " + S_Name + S_ID + " varchar(20)");
            
            st.close();
            co.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    void subTime(int id){
        
        String s=""; 
        try 
        {
                    
            rs = st.executeQuery("Select * from " + id + "_UserSubjects");
            
            while(rs.next()){
               
               s = s.concat(rs.getString("S_Name")+",");
            }
            
            s = s.substring(0, s.length()-2);
            st.executeUpdate("insert into " + id + "_TimeSchedule()" + " values("+s+")");
           
            st.close();
            co.close();
            rs=null;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    void newUser(int id, String username,String password,String name)
    {
       try 
        {
            st.executeUpdate("insert into user values("+id+",'"+username+"', '"+password+"', '"+name+"')");
            st.execute("create table " + id + "_UserSubjects" + "(S_ID int Primary Key, S_Name varchar(40), S_Priority int, S_Time varchar(40))");
            st.execute("create table " + id + "_TimeSchedule" + "(date date Primary Key)");

            st.close();
            co.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void deleteUser(int id) 
    {
        try {
            st.execute("drop table " + id + "_UserSubjects, " + id + "_TimeSchedule");
            st.execute("delete from user where id = " + id);
        } catch (SQLException e) {
        }
    }
    
}