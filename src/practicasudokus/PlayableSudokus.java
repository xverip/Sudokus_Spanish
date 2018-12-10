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
public class PlayableSudokus {
    private final String level;
    private final String description;
    private final String problem;
    private final String solved;

    public PlayableSudokus(String level, String description, String problem, String solved) {
        this.level = level;
        this.description = description;
        this.problem = problem;
        this.solved = solved;
    }

    public String getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public String getProblem() {
        return problem;
    }

    public String getSolved() {
        return solved;
    }
}
