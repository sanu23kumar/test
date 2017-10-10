package scheduler;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandling {

    Statement st;
    Connection co;
    ResultSet rs = null;

    DBHandling() {
        
        try {
            //For Windows, use
            //Class.forName("com.mysql.jdbc.Driver");
            //co=DriverManager.getConnection("jdbc:mysql://localhost:3306/yeah","root","");
            co = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + "sanu" + "?autoReconnect=true&useSSL=false", "root", "kumar2010");//For mac
            st = co.createStatement();

            st.execute("create table if not exists user(id int not null primary key auto_increment, username varchar(20) not null,  password varchar(100) not null, name varchar(20) not null)");

        } catch (Exception ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //connection closed in Scheduler.java
    void addASubject(int id, int S_ID, String S_Name, int S_Priority, String S_Time) {

        try {

            st.executeUpdate("insert into " + id + "_UserSubjects values(" + S_ID + ",'" + S_Name + "'," + S_Priority + ",'" + S_Time + "')");
            st.executeUpdate("ALTER TABLE " + id + "_TimeSchedule ADD " + S_Name + S_ID + " varchar(50)");
//            co.close();
//            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void DeleteASubject(int id, int S_ID, String S_Name) {

        try {

            st.executeUpdate("delete from " + id + "_UserSubjects where S_ID = " + S_ID);
            st.execute("ALTER TABLE " + id + "_TimeSchedule DROP COLUMN " + S_Name + "," + S_ID + " varchar(20)");

//            st.close();
//            co.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void scheduleSubjectsTime(int id) {
        
        try {
            
            st.execute("TRUNCATE TABLE " + id + "_TimeSchedule");
            
            String[] startTimeWeekdays = {"6:00 to 7:00", "7:00 to 8:30", "9:00 to 10:30"};       //To be changed Later after asking user preferences
            String[] startTimeWeekends = {"8:00 to 10:00", "13:00 to 15:00", "9:00 to 10:30"};
            
            int daysToTest = 30, numberOfSubjects, maxPriority = 3;
            LocalDate localDate = LocalDate.now();
            ArrayList priorityList = new ArrayList();
            ArrayList temporaryPriorityList = new ArrayList();
            ArrayList columnData = new ArrayList();
            ArrayList time = new ArrayList();
            
            rs = st.executeQuery("select * from " + id + "_UserSubjects");
            while (rs.next()) {
                priorityList.add(rs.getInt("S_Priority"));
                columnData.add(rs.getString("S_Name").concat(rs.getString("S_ID")));
            }
            
            
            rs = null;
            priorityList.forEach((object) -> {
                temporaryPriorityList.add(object);
            });
            numberOfSubjects = priorityList.size();
            
            //Assuming starting day is monday
            int startingDay = 0;
            for (int scheduleDate = 0; scheduleDate < daysToTest; scheduleDate++) {
               
                int subjects = 0, current = 0;       //Assuming 5 subjects
                int[] selectedSubjects = {0, 0, 0, 0, 0};

                while (subjects < 2) {
//                System.out.println(temporaryPriorityList.get(0));
                    if ((int) temporaryPriorityList.get(current) == maxPriority) {

                        selectedSubjects[current] = 1;

                        temporaryPriorityList.set(current, priorityList.get(current));
                        subjects++;

                    } else {

                        temporaryPriorityList.set(current, (int) (temporaryPriorityList.get(current)) + 1);

                    }

                    if (current < priorityList.size() - 1) {
                        current++;
                    } else {
                        current = 0;
                    }

                }
                
                System.out.println(Arrays.toString(selectedSubjects));
                
                try {
                    int allotCount = 0;

                    //insert date in table.
                    LocalDate scheDate = localDate.plus(scheduleDate, ChronoUnit.DAYS);
                    System.out.println(scheDate.getDayOfWeek());
//                System.out.println(scheDate);

                    st.executeUpdate("insert into " + id + "_TimeSchedule(date) values('" + scheDate + "')");

                    String scheduleString = scheduleDate + ", ";
                    for (int i = 0; i < priorityList.size(); i++) {
                        int temp = selectedSubjects[i];
                        
                        if (temp != 0) {
                            //check later for scheduleDate and Starting day
                            if ((scheduleDate + startingDay) % 6 == 0 || (scheduleDate + startingDay) % 7 == 0) {
                                scheduleString = scheduleString.concat(startTimeWeekends[allotCount] + ", ");
                                time.add(startTimeWeekdays[allotCount]);
                            } else {
                                scheduleString = scheduleString.concat(startTimeWeekdays[allotCount] + ", ");
                                time.add(startTimeWeekdays[allotCount]);
                            }
                            allotCount++;
                        } else {
                            scheduleString = scheduleString.concat("-, ");
                            time.add(startTimeWeekdays[allotCount]);
                            System.out.print(allotCount);

                        }

                        scheduleString = scheduleString.substring(1, scheduleString.length() - 1);
                    }

//                System.out.printf(scheduleString + " subject changed");
//                System.out.println(time);
//                System.out.println(columnData.get(0));
                    for (int i = 0; i < columnData.size(); i++) {
                        st.executeUpdate("update " + id + "_TimeSchedule set " + columnData.get(i) + "='" + time.get(i) + "' where date = '" + scheDate + "'");
//                    System.out.println(columnData.get(i)+" "+time.get(i));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void newUser(int id, String username, String password, String name) {
        try {
            st.executeUpdate("insert into user values(" + id + ",'" + username + "', '" + password + "', '" + name + "')");
            st.execute("create table " + id + "_UserSubjects" + "(S_ID int Primary Key, S_Name varchar(40), S_Priority int, S_Time varchar(40))");
            st.execute("create table " + id + "_TimeSchedule" + "(date date Primary Key)");

//            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void deleteUser(int id) {
        try {
            st.execute("drop table " + id + "_UserSubjects, " + id + "_TimeSchedule");
            st.execute("delete from user where id = " + id);
        } catch (SQLException e) {
        }
    }

    ArrayList<ArrayList<Object>> calendarData(int id) throws SQLException {

        rs = st.executeQuery("select * from " + id + "_TimeSchedule");
        ResultSetMetaData rm = rs.getMetaData();

        int columns = rm.getColumnCount();
        int count = 0;

        ArrayList<ArrayList<Object>> subjectTimings = new ArrayList<>();
        ArrayList<Object> subjectTiming = new ArrayList<>();

        while (rs.next()) {
            subjectTiming.add(rs.getDate(1));
            for (int i = 2; i <= columns; i++) {
                subjectTiming.add(rs.getString(i));
            }
            subjectTimings.add(subjectTiming);
        }

        return subjectTimings;

    }

}
