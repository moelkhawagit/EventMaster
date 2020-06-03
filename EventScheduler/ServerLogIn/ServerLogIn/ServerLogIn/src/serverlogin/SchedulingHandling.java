
package serverlogin;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SchedulingHandling implements Runnable{

    @Override
    public void run() {
        while(true)
        {
            if(!(ServerLogIn.scheduleQueue.isEmpty()))
            {
                try {
                    HandleClient thread = ServerLogIn.scheduleQueue.take();
                    scheduleEvent(thread);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }
    
    private void scheduleEvent(HandleClient thread) throws IOException{
        String eventName = "";
        try{
                    PreparedStatement stmt = ServerLogIn.con.prepareStatement("INSERT INTO reservations VALUES (?, ?, ?, ?, ?, 50)",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    //"2 Room1 P3 2020-05-26 Event name goes here username_follows"
                    stmt.setString(1, thread.requestArray[1]) ;
                    stmt.setString(2, thread.requestArray[3]);
                    stmt.setString(3, thread.requestArray[2]);
                    for(int i=4; i<thread.requestArray.length-1 ;i++){
                    eventName += thread.requestArray[i] + " ";
                    }
                    stmt.setString(4, eventName);
                    stmt.setString(5, thread.requestArray[thread.requestArray.length-1]);
                    stmt.execute();
                    thread.out.writeUTF("scheduled");
                }
                catch(SQLException e){
                    thread.out.writeUTF("error");
                    System.out.println(e);
                }
    }
}
