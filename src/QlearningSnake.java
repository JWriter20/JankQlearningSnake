import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class QlearningSnake extends Spampede {
    public HashMap<String, ArrayList<Double>> qMatrix;
    protected char[][] matrix = new char[30][50];
    public static final char APPLE = 'A';
    public static final char SPACE = 'X';
    public static final char BLOCK = 'B';


    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private final int mazeWidth = 50;
    private final int mazeHeight = 30;
    private final int statesCount = mazeHeight * mazeWidth;

    SpamMaze m;

    public final int reward = 100;
    public final int penalty = -10;

    public int getDistance(int headRow, int headCol, int r, int c) {
        return Math.abs(headRow - r) + Math.abs(headCol - c) - 1;
    }

    public int getColDistance(int headCol, int c) {
        return Math.abs(headCol - c);
    }

    public int getRowDistance(int headRow, int r) {
        return Math.abs(headRow - r);
    }

    public void init() {
        super.init();
        m = super.themaze;
        qMatrix = new HashMap();
        this.m = new SpamMaze(false);
    }

    private String findKey(int r, int c) {
        String key = "";
        boolean hasNotFoundN = true;
        boolean hasNotFoundE = true;
        boolean hasNotFoundW = true;
        boolean hasNotFoundS = true;

        int i = 0;
        while ((hasNotFoundN && getRowDistance(r - i, r) >6)) {
            char curr = m.getContents(r - i, c);
            if (curr == '*' || curr == 'P') {
                key += "N: " + BLOCK + getRowDistance(r - i, r) + " ";
                hasNotFoundN = false;
            } else if (curr == 'D') {
                key += "N: " + APPLE + getRowDistance(r - i, r) + " ";
                hasNotFoundN = false;
            }else if (curr == ' ' && getRowDistance(r-i, r)==5) {
                key += "N: " + SPACE + '5' + " ";
                hasNotFoundN = false;
            }
            i++;
        }

        i = 0;
        while ((hasNotFoundE)) {
            char curr = m.getContents(r, c + i);
            if (curr == '*' || curr == 'P') {
                key += "E: " + BLOCK + getColDistance(c + i, c) + " ";
                hasNotFoundE = false;
            }
            if (curr == 'D') {
                key += "E: " + APPLE + getColDistance(c + i, c) + " ";
                hasNotFoundE = false;
            }else if (curr == ' ' && getColDistance(c + i, c)==5) {
                key += "E: " + SPACE + '5' + " ";
                hasNotFoundE = false;
            }
            i++;
        }

        i = 0;
        while ((hasNotFoundW)) {
            char curr = m.getContents(r + i, c);
            if (curr == '*' || curr == 'P') {
                key += "W: " + BLOCK + getColDistance(c + i, c) + " ";
                hasNotFoundW = false;
            }
            if (curr == 'D') {
                key += "W: " + APPLE + getColDistance(c + i, c) + " ";
                hasNotFoundW = false;
            }else if (curr == ' ' && getColDistance(c + i, c)==5) {
                key += "W: " + SPACE + '5' + " ";
                hasNotFoundW = false;
            }
            i++;
        }

        i = 0;
        while ((hasNotFoundS)) {
            char curr = m.getContents(r, c - i);
            if (curr == '*' || curr == 'P') {
                key += "S: " + BLOCK + getRowDistance(r - i, r) + " ";
                hasNotFoundS = false;
            }
            if (curr == 'D') {
                key += "S: " + APPLE + getRowDistance(r - i, r) + " ";
                hasNotFoundS = false;
            }else if (curr == ' ' && getRowDistance(r - i, r)==5) {
                key += "S: " + SPACE + '5' + " ";
                hasNotFoundS = false;
            }
            i++;
        }
        return key;
    }

    public ArrayList<Double> getValues(String key) {
        if (qMatrix.containsKey(key)) {
            return qMatrix.get(key);
        } else {
            Double Nval = 0.0;
            Double Eval = 0.0;
            Double Wval = 0.0;
            Double Sval = 0.0;

            ArrayList returner = new ArrayList();
            int Ndistance = key.charAt(key.indexOf('N') + 4);
            int Edistance = key.charAt(key.indexOf('E') + 4);
            int Wdistance = key.charAt(key.indexOf('W') + 4);
            int Sdistance = key.charAt(key.indexOf('S') + 4);
            if ((key.charAt(key.indexOf('N') + 3)) == APPLE) {
                Nval += reward;
            }
            if ((key.charAt(key.indexOf('N') + 3)) == BLOCK) {
                if (key.charAt(key.indexOf('E') + 3) == 1)
                    Nval += penalty;
            }
            if ((key.charAt(key.indexOf('E') + 3)) == APPLE) {
                Eval += reward;
            }
            if ((key.charAt(key.indexOf('E') + 3)) == BLOCK) {
                if (key.charAt(key.indexOf('E') + 3) == 1)
                    Eval += penalty;
            }
            if ((key.charAt(key.indexOf('W') + 3)) == APPLE) {
                Wval += reward;
            }
            if ((key.charAt(key.indexOf('W') + 3)) == BLOCK) {
                if (key.charAt(key.indexOf('E') + 3) == 1)
                    Wval += penalty;
            }
            if ((key.charAt(key.indexOf('S') + 3)) == APPLE) {
                Sval += reward;
            }
            if ((key.charAt(key.indexOf('S') + 3)) == BLOCK) {
                if (key.charAt(key.indexOf('E') + 3) == 1)
                    Sval += penalty;
            }
            returner.add(Nval);
            returner.add(Eval);
            returner.add(Wval);
            returner.add(Sval);
            return returner;

        }

    }


    private void updateQmatrix(int r, int c, char dir) { //n=0, e= 1, w=2, s=3
        String currState = findKey(r, c);
        ArrayList<Double> valList;
        if (qMatrix.containsKey(findKey(r, c))) {
            valList = qMatrix.get(currState);
        } else {
            valList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                valList.add(0.0);
            }
            // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
            int nextR = r;
            int nextC = c;
            int index;

            if (dir == NORTH) {
                nextR--;
                index = 0;
            } else if (dir == EAST) {
                nextC++;
                index = 1;
            } else if (dir == WEST) {
                nextC--;
                index = 2;
            } else {
                nextR++;
                index = 3;
            }
            double qUpdate = valList.get(index) + alpha * (getReward(r, c) + (getMax(nextR, nextC)[0]) - valList.get(index));
            valList.set(index, qUpdate);
            qMatrix.put(currState, valList);
        }

    }

    private double[] getMax(int r, int c) {
        String sPrime = findKey(r, c);
        double currDir = -999.0;
        double[] returnVal = new double[2];
        if (qMatrix.containsKey(sPrime)) {
            double max = qMatrix.get(sPrime).get(0);
            for (int i = 0; i < qMatrix.get(sPrime).size(); i++) {
                if (max < qMatrix.get(sPrime).get(i)) {
                    max = qMatrix.get(sPrime).get(i);
                    currDir = i * 1.0;
                }
            }
            returnVal[0] = max;
            returnVal[1] = currDir;

            return returnVal;
        } else {
            return new double[]{0, -1};
        }
    }

    private double getReward(int r, int c) {
        if (m.getContents(r, c) == m.WALL || m.getContents(r, c) == m.BODY) {
            return penalty;
        } else if (m.getContents(r, c) == m.SPAM) {
            return reward;
        } else {
            return 0;
        }

    }

    public void cycle() {
        //@override
        final int numRuns = 100000;
        int currRun = 0;
        Random random1 = new Random();
        while(currRun<numRuns){
        boolean goRand = false;
        int rand = random1.nextInt((int) (Math.pow(2.718, (currRun / (numRuns / 4))) + 0.5));
        if (rand == 0) {
            goRand = true;
        }
        int r = themaze.getHeadRow();
        int c = themaze.getHeadCol();

        ArrayList<Character> choices = new ArrayList<>();
        choices.add(NORTH);
        choices.add(EAST);
        choices.add(WEST);
        choices.add(SOUTH);
        char randChoice;
        if (themaze.dir == themaze.NORTH) {
            choices.remove(new Character(SOUTH));
            if (goRand) {
                //do nothing for now
            } else {
                r--;
            }

        }
        if (themaze.dir == themaze.EAST) {
            choices.remove(new Character(WEST));
            if (goRand) {
                //do nothing for now
            } else {
                c--;
            }

        }
        if (themaze.dir == themaze.WEST) {
            choices.remove(new Character(EAST));
            if (goRand) {
                //do nothing for now
            } else {
                c++;
            }

        }
        if (themaze.dir == themaze.SOUTH) {
            choices.remove(new Character(SOUTH));
            if (goRand) {
                //do nothing for now
            } else {
                r++;
            }

        }

        randChoice = choices.get(random1.nextInt(3));
        int nextMaxDir = (int) getMax(r, c)[1];
        //System.out.println(nextMaxDir);
        char nextDir = 'k';
        if (goRand || nextMaxDir == -1) {
            nextDir = randChoice;
        } else {
            switch (nextMaxDir) {
                case 0:
                    nextDir = NORTH;
                    break;
                case 1:
                    nextDir = EAST;
                    break;
                case 2:
                    nextDir = WEST;
                    break;
                case 3:
                    nextDir = SOUTH;
                    break;
            }
        }
        updateQmatrix(r, c, nextDir);
        super.cycle();
        char d; //next next direction
        if (nextDir == NORTH) d = themaze.NORTH;
        else if (nextDir == EAST) d = themaze.EAST;
        else if (nextDir == WEST) d = themaze.WEST;
        else d = themaze.SOUTH;

        themaze.dir = d;
        super.dir = d;
        go();
        currRun++;
            System.out.println(qMatrix);
        if(currRun==numRuns-1){
            pause();
        }
       }
    }
}
