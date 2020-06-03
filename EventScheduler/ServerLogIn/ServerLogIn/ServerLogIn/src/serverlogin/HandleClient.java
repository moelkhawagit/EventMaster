
package serverlogin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class HandleClient implements Runnable{
   
    private final Socket socket;
    public DataInputStream in;
    public DataOutputStream out;
    public String request;
    public String requestArray[];
    private PreparedStatement stmt;
    private ResultSet rs;
    
    
    public HandleClient(Socket socket) throws SQLException{
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        while(true){
            request = in.readUTF();
            requestArray = request.split(" ");
            switch(requestArray[0]){
                case "0": logInCheck();break;
                case "1": registerNewUser();break;
                case "2": displayAvailableSlots();break;
                case "3": ServerLogIn.scheduleQueue.put(this); break;
                case "23": searchForAnEvent();break;
                case "25": bookATicket();break;
                case "5": displayUsersScheduledEvents();break;
            }
        }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    private synchronized void logInCheck() throws IOException{
        try{
            stmt = ServerLogIn.con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1,requestArray[1]) ;
            stmt.setString(2, requestArray[2]);
            rs = stmt.executeQuery();
            if(rs.next()){
                out.writeUTF(rs.getString("first_name")+ " " + rs.getString("last_name"));
                stmt = ServerLogIn.con.prepareStatement("DELETE FROM reservations WHERE Event_date < NOW() AND username = ?");
                stmt.setString(1,requestArray[1]) ;
                stmt.executeUpdate();
            }
            else{
                out.writeUTF("decline access");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }
    private synchronized void registerNewUser() throws IOException{
         try{
            stmt = ServerLogIn.con.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, requestArray[1]) ;
            stmt.setString(2, requestArray[2]);
            stmt.setString(3, requestArray[5]);
            stmt.setString(4, requestArray[6]);
            stmt.setString(5, requestArray[7]);
            stmt.setString(6, requestArray[3]);
            stmt.setString(7, requestArray[4]);
            stmt.execute();
            out.writeUTF("Registered");
        }
        catch(SQLException e){
            out.writeUTF("Username already exists");
            System.out.println(e);
        }
    }
    private synchronized void displayAvailableSlots() throws IOException{
        String response = "";
        try{
            stmt = ServerLogIn.con.prepareStatement("SELECT * FROM reservations WHERE room_id=? AND Event_date=? ORDER BY period ASC",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, requestArray[1]) ;
            stmt.setString(2, requestArray[2]);
            rs = stmt.executeQuery();
            while(rs.next())
                response = response + rs.getString("Period")+ " " +rs.getString("Event_name") +"\n";
            out.writeUTF(response);
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }


    private synchronized void bookATicket() throws IOException {
       try{
            stmt = ServerLogIn.con.prepareStatement("UPDATE reservations SET capacity = capacity-1 WHERE (Period = ? AND Event_date = ? AND Event_name = ?)");
            stmt.setString(1, requestArray[1]) ;
            stmt.setString(2, requestArray[2]);
            stmt.setString(3, requestArray[3]);
            stmt.execute();
            out.writeUTF("Success");
        }
        catch(SQLException e){
            out.writeUTF("error");
            System.out.println(e);
        }
    }

    private synchronized void displayUsersScheduledEvents() throws IOException {
        String response = "";
        try{
            stmt = ServerLogIn.con.prepareStatement("SELECT * FROM reservations WHERE username=? ORDER BY event_date ASC, period ASC",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, requestArray[1]);
            rs = stmt.executeQuery();
            while(rs.next())
                response = response + rs.getString("Event_name")+ " " +rs.getString("Event_date") + " " +rs.getString("Period") + " " + rs.getString("Room_id")+"\n";
            out.writeUTF(response);
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }

    private synchronized void searchForAnEvent() throws IOException {
        try{
            stmt = ServerLogIn.con.prepareStatement("SELECT * FROM reservations WHERE event_name = ?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, requestArray[1]) ;
            rs = stmt.executeQuery();
            if(rs.next()){
                out.writeUTF("Event1");
            }
            else{
                out.writeUTF("Event0");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }
    
}
