package edu.ws;

import edu.logic.DBConnector;
import edu.logic.DocumentHandler;
import edu.logic.Setting;
import edu.logic.PropertiesTool;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
/**
 *
 * @author lmparra
 *
 * <code>FileServerImpl</code> class implements <code>FileServer</code> class
 * to create a web service that allow upload and download files.
 */

//Service Implementation Bean
@MTOM
@WebService(endpointInterface = "edu.ws.FileServer", serviceName="FSImpl")
public class FileServerImpl implements FileServer{

    private PropertiesTool prop;
    private String fileName = Setting.BASE_PATH + Setting.PROPERTIES_FILE;
    private InputStream in;
    private DocumentHandler document;
    private DBConnector connector;
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet result;
    private List<String> intFields,stringFields;

    public FileServerImpl() throws IOException {
        prop = new PropertiesTool(fileName);
    }

    @Override
    public byte[] downloadFile(String name) {
        document = new DocumentHandler(prop.getProperty("ws.dl_folder"), name);
        try {
            return document.readDocument();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String uploadFile(byte[] data, String name) {
        document = new DocumentHandler(name, prop.getProperty("ws.dl_folder"));

        return document.writeDocument(data);
    }

  @Override
  public int[] getUserFiles(int uid) {

        int[] filesObject = {};
        connector = new DBConnector();
        connection = null;

        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement("SELECT idDocument FROM user_document " +
                        "WHERE idUser = ? ");
            pstmt.setFloat(1, uid);
            result = pstmt.executeQuery();

            intFields = Arrays.asList("idDocument");


            ArrayDeque userdata = new ArrayDeque();

            while(result.next()) {
              for(String field : intFields){
                userdata.add(result.getInt(field));
              }
            }

            filesObject = new int[userdata.size()];
            int i = 0;
            for (Iterator it = userdata.iterator(); it.hasNext();) {
                Object object = it.next();
                filesObject[i] = (Integer)object;
                System.out.println(filesObject[i]);
                i++;
            }

            connection.commit();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UserServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return filesObject;
  }

  @Override
  public boolean setFileState(int idDocument, int state) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String[] getDocument(int idDocument) {

        String[] filesObject = {};
        connector = new DBConnector();
        connection = null;

        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement("SELECT idDocument, name, type, path,state, created FROM document " +
                        "WHERE idDocument = ? ");
            pstmt.setFloat(1, idDocument);
            result = pstmt.executeQuery();

            intFields = Arrays.asList("idDocument");
            stringFields = Arrays.asList( "name", "type", "path", "state");


            ArrayDeque userdata = new ArrayDeque();

            if(result.next()){
              for(String field : intFields){
                userdata.add(""+result.getInt(field));
              }
              for(String field : stringFields){
                  userdata.add(result.getString(field));
              }
              Date date = (Date)result.getDate("created");
              userdata.add(date.toString());
            }

            filesObject = (String[])userdata.toArray(new String[]{});

            connection.commit();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
          Logger.getLogger(FileServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UserServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return filesObject;
  }


}
