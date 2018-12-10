package practicasudokus;

import Historial.History;
import Historial.History.Game;
import Sudokus.Sudokus;
import Sudokus.Sudokus.Sudoku;
import Usuarios.Users.User;
import Usuarios.Users;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author alu2015508
 */
public class FileFunctions {

    /**
     * Función principal del programa, donde aparece el menu de opciones si no
     * estás loggeado
     */
    public static void startProgram() {
        Users users = new Users();
        History history = new History();

        int selectedOption;
        do {
            menuIfNotLogged();
            selectedOption = askInteger("Selecciona una opción: ");
            switch (selectedOption) {
                case 1:
                    System.out.println("REGISTRAR USUARIO");
                    registerUser(users);
                    break;
                case 2:
                    System.out.println("VALIDARSE");
                    Object[] thingsINeed = validateUser();
                    if ((boolean) thingsINeed[0]) {
                        correctLogin((MyUsers) thingsINeed[3], (String) thingsINeed[1], (String) thingsINeed[2], users, history);
                    } else {
                        System.out.println("Alguno de los parámetro es erroneo");
                        System.out.println("");
                    }
                    break;
                case 3:
                    System.out.println("RANKING");
                    showRanking();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        } while (selectedOption != 4);
    }

    /**
     * Función que ordena los jugadores según la media
     */
    public static void showRanking() {
        List names = new ArrayList<>();

        for (MyHistory x : PracticaSudokus.myHistory) {
            if (!names.contains(x.getUsername())) {
                names.add(x.getUsername());
            }
        }

        Object[][] sorting = new Object[names.size()][2];

        for (int x = 0; x < names.size(); x++) {
            long time = 0;
            int counter = 0;
            for (MyHistory y : PracticaSudokus.myHistory) {
                if (y.getUsername().equals((String) names.get(x))) {
                    time = time + Long.parseLong(y.getTimeInNano());
                    counter++;
                }
            }
            double aux = (time / counter) / 1e9;
            String timeparsed = Double.toString(aux);
            sorting[x][0] = (String) names.get(x);
            sorting[x][1] = timeparsed;
        }

        for (int y = 0; y < names.size(); y++) {
            for (int x = 0; x < names.size() - 1; x++) {
                Double first = Double.parseDouble((String)sorting[x][1]);
                Double second = Double.parseDouble((String)sorting[x+1][1]);
                if (first > second) {
                    String firstthing = (String) sorting[x][0];
                    String secondthing = (String) sorting[x][1];
                    sorting[x][0] = sorting[x + 1][0];
                    sorting[x][1] = sorting[x + 1][1];
                    sorting[x + 1][0] = firstthing;
                    sorting[x + 1][1] = secondthing;
                }
            }
        }

        if (names.size() < 10) {
            rank(sorting, names.size());
        } else {
            rank(sorting, 10);
        }
    }

    /**
     * Función donde pasas un array bidimensional de objectos y un maximo y te
     * lo imprime
     *
     * @param myArray Array de objectos
     * @param max Máximo numero de repeticiones
     */
    public static void rank(Object[][] myArray, int max) {
        for (int x = 0; x < max; x++) {
            System.out.println(x + 1 + ".- " + (String) myArray[x][0] + " | Media: " + (String) myArray[x][1] + " segundos");
        }
        System.out.println("");
    }

    /**
     * Segunda función principal del programa, donde aparece el menú de opciones
     * si estás loggeado
     *
     * @param user Objecto de tipo usuario
     * @param username Nombre del usuario
     * @param password Contraseña del usuario
     * @param users Raiz de JAXB para los usuarios
     * @param history Raiz de JAXB para el historial
     */
    public static void correctLogin(MyUsers user, String username, String password, Users users, History history) {
        int selectedOption;
        do {
            menuIfLogged();
            selectedOption = askInteger("Selecciona una opción: ");
            switch (selectedOption) {
                case 1:
                    System.out.println("MODIFICAR CONTRASEÑA");
                    password = changePassword(user, password, users);
                    break;
                case 2:
                    System.out.println("JUGAR SUDOKU");
                    playSudoku(username, history);
                    break;
                case 3:
                    System.out.println("VER PROMEDIO");
                    miPromedio(username);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        } while (selectedOption != 4);
    }

    /**
     * Función donde pasamos un nombre de usuario y nos devuelve su promedio de
     * finalización de sudokus
     *
     * @param username Nombre de usuario
     */
    public static void miPromedio(String username) {
        long total = 0;
        int counter = 0;
        for (MyHistory x : PracticaSudokus.myHistory) {
            if (x.getUsername().equals(username)) {
                total = total + Long.parseLong(x.getTimeInNano());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("NO HAS JUGADO NINGUNA PARTIDA");
        } else {
            System.out.println("Tu promedio es de " + (double) (total / counter) / 1e9 + " minutos");
        }
    }

    /**
     * Función que da comienzo a jugar un sudoku que el usuario indicado no haya
     * jugado y/o resuelto
     *
     * @param username Nombre de usuario
     * @param history Raiz de JAXB para el historial
     */
    public static void playSudoku(String username, History history) {
        System.out.println("Buscando sudoku...");
        String myProblem;
        String mySolution;
        PlayableSudokus foundSudoku;
        boolean foundASudoku = true;
        do {
            int possibleSudoku = (int) (Math.random() * ((PracticaSudokus.mySudokus.size() - 0) + 1)) + 0;
            foundSudoku = PracticaSudokus.mySudokus.get(possibleSudoku);
            foundASudoku = isPlayed(foundSudoku, username);
        } while (foundASudoku);

        myProblem = foundSudoku.getProblem();
        mySolution = foundSudoku.getSolved();
        String userInput = myProblem.replace(".", " ");

        List invalidPosition = new ArrayList<>();
        for (int x = 0; x < myProblem.length(); x++) {
            if (myProblem.charAt(x) != '.') {
                invalidPosition.add(x);
            }
        }
        System.out.println("Sudoku encontrado | Nivel " + foundSudoku.getLevel() + " (" + foundSudoku.getDescription() + ")");

        boolean playing = true;
        long startTime = System.nanoTime();
        do {
            //DESCOMENTAR ESTA LINEA SI QUIERES VER LA SOLUCIÓN ENCIMA DEL SUDOKU
            //System.out.println(mySolution);
            printSudoku(userInput);
            int positionToChange = askCoordinate("Selecciona una coordenada (FF para rendirte, GG para corregir, BB para autocompletar): ", invalidPosition);
            switch (positionToChange) {
                case 600:
                    System.out.println("SUERTE LA PRÓXIMA VEZ!");
                    playing = false;
                    break;
                case 700:
                    System.out.println("VEAMOS COMO LO HAS HECHO");
                    if (userInput.equals(mySolution)) {
                        System.out.println("RESULTADO CORRECTO!");
                        long estimatedTime = System.nanoTime() - startTime;
                        double timeInSeconds = (double) estimatedTime / 1e9;
                        String estimatedTimeUpdate = Long.toString(estimatedTime);
                        System.out.println("Lo has solucionado en " + timeInSeconds + " segundos!");
                        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss").format(Calendar.getInstance().getTime());
                        PracticaSudokus.myHistory.add(new MyHistory(estimatedTimeUpdate, timeStamp, username, myProblem, mySolution));
                        reWriteHistory(new File("history.xml"), history);
                        playing = false;
                    } else {
                        System.out.println("SIGUE INTENTANDOLO");
                    }
                    break;
                case 900:
                    System.out.println("ESO ES TRAMPA, PERO VENGA VA, TE CUENTA IGUAL");
                    printSudoku(mySolution);
                    long estimatedTime = System.nanoTime() - startTime;
                    double timeInSeconds = (double) estimatedTime / 1e9;
                    String estimatedTimeUpdate = Long.toString(estimatedTime);
                    System.out.println("Lo has solucionado en " + timeInSeconds + " segundos!");
                    String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss").format(Calendar.getInstance().getTime());
                    PracticaSudokus.myHistory.add(new MyHistory(estimatedTimeUpdate, timeStamp, username, myProblem, mySolution));
                    reWriteHistory(new File("history.xml"), history);
                    playing = false;
                    break;
                default:
                    int wantedNumber = askNumberBetween();
                    char ch = Character.forDigit(wantedNumber, 10);
                    String newInput = userInput.substring(0, positionToChange) + ch + userInput.substring(positionToChange + 1);
                    userInput = newInput;
                    break;
            }
        } while (playing);
    }

    /**
     * Función para reescribir el archivo de historial
     *
     * @param f Nombre del archivo
     * @param history Raiz de JAXB para historial
     */
    public static void reWriteHistory(File f, History history) {
        history = new History();
        f.delete();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.close();
        } catch (IOException ex) {
        }

        for (MyHistory x : PracticaSudokus.myHistory) {
            Game newGame = new Game();
            newGame.setUsername(x.getUsername());
            newGame.setDate(x.getDateOfSolving());
            newGame.setProblem(x.getProblem());
            newGame.setSolved(x.getSolved());
            newGame.setTime(x.getTimeInNano());
            history.getGame().add(newGame);
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(History.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(history, f);
        } catch (JAXBException ex) {
        }
    }

    /**
     * Función para preguntar un numero dentro de un rango de 1 - 9 (incluidos)
     *
     * @return
     */
    public static int askNumberBetween() {
        int number = 0;
        boolean numberOk = false;
        do {
            number = askInteger("Introduce un valor entre 1 y 9");
            if (number >= 1 && number <= 9) {
                numberOk = true;
            }
        } while (!numberOk);
        return number;
    }

    /**
     * Función para preguntar una coordenada válida del sudoku
     *
     * @param message Mensaje mostrado para preguntar
     * @param x Lista de posiciones del sudoku donde los números no pueden ser
     * modificados
     * @return Devuelve la posicion del sudoku de la coordenada indicada
     */
    public static int askCoordinate(String message, List x) {
        String cadena;
        int position = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean coordinateOk = false;
        do {
            System.out.println(message);
            cadena = null;
            try {
                cadena = br.readLine();
            } catch (IOException ex) {
            }
            if (cadena.length() == 2) {
                switch (cadena.toUpperCase().charAt(0)) {
                    case 'A':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) - 1;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'B':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 8;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else if (cadena.toUpperCase().charAt(1) == 'B') {
                            position = 900;
                            coordinateOk = true;
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'C':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 17;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'D':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 26;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'E':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 35;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'F':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 44;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else if (cadena.toUpperCase().charAt(1) == 'F') {
                            position = 600;
                            coordinateOk = true;
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'G':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 53;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else if (cadena.toUpperCase().charAt(1) == 'G') {
                            position = 700;
                            coordinateOk = true;
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'H':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 62;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    case 'I':
                        if (isNumber(cadena.charAt(1))) {
                            position = Integer.parseInt(String.valueOf(cadena.charAt(1))) + 71;
                            if (x.contains(position)) {
                                System.out.println("No puedes modificar un número de serie");
                            } else {
                                coordinateOk = true;
                            }
                        } else {
                            System.out.println("Coordenada no válida");
                        }
                        break;
                    default:
                        System.out.println("Coordenadas no válidas");
                        break;
                }
            } else {
                System.out.println("Coordenadas no válidas");
            }
        } while (!coordinateOk);
        return position;
    }

    /**
     * Función para comprobar si un char es numérico o no
     *
     * @param number Char a comprobar
     * @return Devuelve un booleano en función del resultado
     */
    public static boolean isNumber(char number) {
        try {
            int d = Integer.parseInt(String.valueOf(number));
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Función que imprime por pantalla el estado el sudoku según los inpus del
     * usuario
     *
     * @param userInput Números que el usuario ya ha introducido
     */
    public static void printSudoku(String userInput) {
        char[] numbers = userInput.toCharArray();
        System.out.println("");
        System.out.println("  1 2 3 4 5 6 7 8 9");
        System.out.println(" +-----+-----+-----+");
        System.out.println("A|" + numbers[0] + " " + numbers[1] + " " + numbers[2] + "|" + numbers[3] + " " + numbers[4] + " " + numbers[5] + "|" + numbers[6] + " " + numbers[7] + " " + numbers[8] + "|");
        System.out.println("B|" + numbers[9] + " " + numbers[10] + " " + numbers[11] + "|" + numbers[12] + " " + numbers[13] + " " + numbers[14] + "|" + numbers[15] + " " + numbers[16] + " " + numbers[17] + "|");
        System.out.println("C|" + numbers[18] + " " + numbers[19] + " " + numbers[20] + "|" + numbers[21] + " " + numbers[22] + " " + numbers[23] + "|" + numbers[24] + " " + numbers[25] + " " + numbers[26] + "|");
        System.out.println(" +-----+-----+-----+");
        System.out.println("D|" + numbers[27] + " " + numbers[28] + " " + numbers[29] + "|" + numbers[30] + " " + numbers[31] + " " + numbers[32] + "|" + numbers[33] + " " + numbers[34] + " " + numbers[35] + "|");
        System.out.println("E|" + numbers[36] + " " + numbers[37] + " " + numbers[38] + "|" + numbers[39] + " " + numbers[40] + " " + numbers[41] + "|" + numbers[42] + " " + numbers[43] + " " + numbers[44] + "|");
        System.out.println("F|" + numbers[45] + " " + numbers[46] + " " + numbers[47] + "|" + numbers[48] + " " + numbers[49] + " " + numbers[50] + "|" + numbers[51] + " " + numbers[52] + " " + numbers[53] + "|");
        System.out.println(" +-----+-----+-----+");
        System.out.println("G|" + numbers[54] + " " + numbers[55] + " " + numbers[56] + "|" + numbers[57] + " " + numbers[58] + " " + numbers[59] + "|" + numbers[60] + " " + numbers[61] + " " + numbers[62] + "|");
        System.out.println("H|" + numbers[63] + " " + numbers[64] + " " + numbers[65] + "|" + numbers[66] + " " + numbers[67] + " " + numbers[68] + "|" + numbers[69] + " " + numbers[70] + " " + numbers[71] + "|");
        System.out.println("I|" + numbers[72] + " " + numbers[73] + " " + numbers[74] + "|" + numbers[75] + " " + numbers[76] + " " + numbers[77] + "|" + numbers[78] + " " + numbers[79] + " " + numbers[80] + "|");
        System.out.println(" +-----+-----+-----+");
        System.out.println("");

    }

    /**
     * Función para comprobar si un sudoku ha sido o no jugado por un usuario
     * indicado
     *
     * @param sudoku Objeto de tipo sudoku
     * @param username Nombre de usuario
     * @return devuelve un booleano en función del resultado
     */
    public static boolean isPlayed(PlayableSudokus sudoku, String username) {
        boolean found = false;
        for (MyHistory x : PracticaSudokus.myHistory) {
            if (username.equals(x.getUsername())) {
                if (sudoku.getProblem().equals(x.getProblem()) && sudoku.getSolved().equals(x.getSolved())) {
                    found = true;
                }
            }
        }

        return found;
    }

    /**
     * Función para cambiar la contraseña a un usuario registrado
     *
     * @param user Objecto de tipo usuario
     * @param password Contraseña del usuario
     * @param users Raiz de JAXB para usuarios
     * @return
     */
    public static String changePassword(MyUsers user, String password, Users users) {
        boolean loopController = true;
        do {
            String actualPassword = askString("Contraseña actual: ");
            if (actualPassword.equals(password)) {
                loopController = false;
            } else {
                System.out.println("Las contraseñas no coinciden");
            }
        } while (loopController);

        loopController = true;
        String newPassword;
        do {
            newPassword = askString("Nueva contraseña: ");
            String validate = askString("Confirmar contraseña: ");
            if (newPassword.equals(validate)) {
                loopController = false;
            } else {
                System.out.println("Las contraseñas no coinciden");
            }
        } while (loopController);

        user.setPassword(newPassword);
        File f = new File("users.xml");
        reWriteUsers(f, users);
        System.out.println("Contraseña modificada con éxito\n");
        return newPassword;
    }

    /**
     * Función para validar el loggin de un usuario
     *
     * @return Devuelve un array de objetos, donde 0 = boolean, 1 = nombre de
     * usuario, 2 = contraseña, 3 = (si existe) objeto del usuario
     */
    public static Object[] validateUser() {
        boolean validLogin = false;
        String username = askString("Nombre de usuario: ");
        String password = askString("Contraseña: ");
        Object[] myThings = new Object[4];

        for (MyUsers x : PracticaSudokus.myUsers) {
            if (x.getUsername().equals(username) && x.getPassword().equals(password)) {
                validLogin = true;
                myThings[3] = x;
            }
        }

        myThings[0] = validLogin;
        myThings[1] = username;
        myThings[2] = password;
        return myThings;
    }

    /**
     * Función para registrar un usuario
     *
     * @param users Raiz JAXB para usuarios
     */
    public static void registerUser(Users users) {
        File f = new File("users.xml");
        boolean loopController = true;
        String username;
        String password;
        do {
            boolean exists = false;
            username = askString("Nombre de usuario deseado: ");
            for (MyUsers x : PracticaSudokus.myUsers) {
                if (x.getUsername().equalsIgnoreCase(username)) {
                    System.out.println("Ya existe este nombre de usuario");
                    exists = true;
                }
            }
            if (!exists) {
                loopController = false;
            }
        } while (loopController);

        password = askString("Contraseña deseada: ");
        PracticaSudokus.myUsers.add(new MyUsers(username, password));
        reWriteUsers(f, users);
        System.out.println("Usuario registrado con éxito\n");
    }

    /**
     * Función para reescribir el archivo de usuarios
     *
     * @param f Nombre del archivo
     * @param users Raiz JAXB de usuarios
     */
    public static void reWriteUsers(File f, Users users) {
        users = new Users();
        f.delete();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.close();
        } catch (IOException ex) {
        }

        for (MyUsers x : PracticaSudokus.myUsers) {
            User newUser = new User();
            newUser.setUsername(x.getUsername());
            newUser.setPassword(x.getPassword());
            users.getUser().add(newUser);
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(users, f);
        } catch (JAXBException ex) {
        }
    }

    /**
     * Función para pedir un String al usuario
     *
     * @param input mensaje con el que pedimos el input
     * @return devuelve el string introducido por el usuario
     */
    public static String askString(String input) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(input);
        String cadena = null;
        try {
            cadena = br.readLine();
        } catch (IOException ex) {

        }
        return cadena;
    }

    /**
     * Función para pedir un Integer al usuario
     *
     * @param input mensaje con el que pedimos el input
     * @return devuelve el integer introducido por el usuario
     */
    public static int askInteger(String input) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int number = 0;
        boolean error;
        do {
            System.out.println(input);
            error = false;
            try {
                number = Integer.parseInt(br.readLine());
                error = true;
            } catch (IOException | NumberFormatException ex) {
                System.out.println("Input invalido");
            }
        } while (!error);
        return number;
    }

    /**
     * Menú de opciones para cuando NO estás loggeado
     */
    public static void menuIfNotLogged() {
        System.out.println("1- Registrarse");
        System.out.println("2- Iniciar sesion");
        System.out.println("3- Ver rankings");
        System.out.println("4- Salir");
    }

    /**
     * Menú de opciones para cuando SI estás loggeado
     */
    public static void menuIfLogged() {
        System.out.println("1- Modificar contraseña");
        System.out.println("2- Jugar sudoku");
        System.out.println("3- Ver tu promedio");
        System.out.println("4- Salir al menú");
    }

    /**
     * Función que carga en memoria los archivos necesarios para el
     * funcionamiento del programa
     */
    public static void addToMemory() {
        File f = new File("sudokus.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sudokus.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Sudokus sdks = (Sudokus) jaxbUnmarshaller.unmarshal(f);
            for (Sudoku x : sdks.getSudoku()) {
                PracticaSudokus.mySudokus.add(new PlayableSudokus(x.getLevel(), x.getDescription(), x.getProblem(), x.getSolved()));
            }
        } catch (JAXBException ex) {
        }

        f = new File("users.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Users srs = (Users) jaxbUnmarshaller.unmarshal(f);
            for (User x : srs.getUser()) {
                PracticaSudokus.myUsers.add(new MyUsers(x.getUsername(), x.getPassword()));
            }
        } catch (JAXBException ex) {
        }

        f = new File("history.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(History.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            History hstry = (History) jaxbUnmarshaller.unmarshal(f);
            for (Game x : hstry.getGame()) {
                PracticaSudokus.myHistory.add(new MyHistory(x.getTime(), x.getDate(), x.getUsername(), x.getProblem(), x.getSolved()));
            }
        } catch (JAXBException ex) {
        }
    }

    /**
     * Función que se ejecuta hasta que todos los archivos necesarios han sido
     * añadidos
     */
    public static void doesExist() {
        boolean aux = true;

        while (aux) {
            aux = archivos();
        }

        System.out.println("Carga de archivos completada");
    }

    /**
     * Función que da al usuario a elegir si quiere añadir los archivos
     * manualmente o automaticamente
     *
     * @return devuelve un boolean en función de si los archivos han sido
     * agregados correctamente o no
     */
    public static boolean archivos() {
        Scanner sc = new Scanner(System.in);
        boolean wantToReload = false;
        File[] myFiles = {new File("sudokus.xml"), new File("users.xml"), new File("history.xml")};

        for (File x : myFiles) {
            if (x.exists()) {
                System.out.println("Archivo (" + x + ") cargado con éxito");
            } else {
                wantToReload = true;
                System.out.println("ERROR: La carga del archivo (" + x + ")ha fallado");
            }
        }

        if (wantToReload) {
            System.out.println("Parece que no existe/n algun/os archivo/s");
            boolean managingFiles = true;
            do {
                System.out.print("Deseas añadir los archivos automaticamente(1) o manualmente(2)?");
                switch (sc.nextLine()) {
                    case "1":
                        addXMLFiles(myFiles);
                        managingFiles = false;
                        break;
                    case "2":
                        System.out.println("Añade los archivos y apreta enter");
                        sc.nextLine();
                        managingFiles = false;
                        break;
                    default:
                        break;
                }
            } while (managingFiles);
        }

        return wantToReload;
    }

    /**
     * Función que añade los archivos xml automaticamente
     *
     * @param x array de archivos a añadir
     */
    public static void addXMLFiles(File[] x) {
        boolean sudokusxml = false;
        for (File y : x) {
            if (y.exists()) {
                if (y.getName().equals("sudokus.xml")) {
                    sudokusxml = true;
                }
                System.out.println(y + " ya existe");
            } else {
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(y));
                    System.out.println(y + " añadido con éxito");
                    bw.close();
                } catch (IOException ex) {
                }
            }
        }
        if (!sudokusxml) {
            boolean txtExist = false;
            Scanner sc = new Scanner(System.in);
            File txtFile = new File("sudokus.txt");
            while (!txtExist) {
                if (txtFile.exists()) {
                    txtExist = true;
                } else {
                    System.out.println(txtFile + " no existe.");
                    System.out.println("Añade el archivo " + txtFile + " y pulsa enter");
                    sc.nextLine();
                }
            }
            try {
                Sudokus sudokus = new Sudokus();
                Sudoku newSudoku = null;
                int position = 0;
                String data;
                try (FileReader fr = new FileReader(txtFile); BufferedReader br = new BufferedReader(fr)) {
                    while ((data = br.readLine()) != null) {
                        switch (position) {
                            case 0:
                                newSudoku = new Sudoku();
                                String[] firstLineParts = data.split(" ");
                                newSudoku.setDescription(firstLineParts[2]);
                                newSudoku.setLevel(firstLineParts[1]);
                                position++;
                                break;
                            case 1:
                                newSudoku.setProblem(data);
                                position++;
                                break;
                            case 2:
                                newSudoku.setSolved(data);
                                position = 0;
                                sudokus.getSudoku().add(newSudoku);
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println(":(");
                }
                JAXBContext jaxbContext = JAXBContext.newInstance(Sudokus.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(sudokus, x[0]);
            } catch (JAXBException ex) {
                System.out.println(":(");
            }
        }
    }
}
