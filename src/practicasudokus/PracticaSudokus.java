package practicasudokus;

import java.util.ArrayList;

/**
 *
 * @author alu2015508
 */
public class PracticaSudokus {

    public static ArrayList<PlayableSudokus> mySudokus;
    public static ArrayList<MyUsers> myUsers;
    public static ArrayList<MyHistory> myHistory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mySudokus = new ArrayList<>();
        myUsers = new ArrayList<>();
        myHistory = new ArrayList<>();
        
        FileFunctions.doesExist();
        FileFunctions.addToMemory(); 
        FileFunctions.startProgram();
    }

}
