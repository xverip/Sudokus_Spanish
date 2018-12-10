/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicasudokus;

/**
 *
 * @author alu2015508
 */
public class MyHistory {
    private final String timeInNano;
    private final String dateOfSolving;
    private final String username;
    private final String problem;
    private final String solved;

    public MyHistory(String timeInMilis, String dateOfSolving, String username, String problem, String solved) {
        this.timeInNano = timeInMilis;
        this.dateOfSolving = dateOfSolving;
        this.username = username;
        this.problem = problem;
        this.solved = solved;
    }

    public String getTimeInNano() {
        return timeInNano;
    }

    public String getDateOfSolving() {
        return dateOfSolving;
    }

    public String getUsername() {
        return username;
    }

    public String getProblem() {
        return problem;
    }

    public String getSolved() {
        return solved;
    }  
}
