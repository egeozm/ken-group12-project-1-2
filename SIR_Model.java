import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
Make sure the 'infected' value is greater than 0.
Code still doesn't work for large values (believe this is a fault of the model itself).
 */
public class SIR_Model {

    double susceptible;
    double infected;
    double recovered;
    double stepSize;
    int currentSteps;

    double S0;
    double I0;
    double R0;

    Random randomNumber = new Random();
    double gamma = 0.1;
    double k = 0.02;
    double mu = 0.001;
    double s_dot;
    double i_dot;
    double r_dot;

    public SIR_Model(double susceptible, double infected, double recovered, int stepSize) {

        this.susceptible = susceptible;
        this.infected = infected;
        this.recovered = recovered;
        this.stepSize = stepSize;
        this.currentSteps = 0;

        this.S0 = susceptible;
        this.I0 = infected;
        this.R0 = recovered;


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                System.out.println(changeInVariables());
                currentSteps++;
                if(currentSteps == steps) {
                    System.out.println(changeInVariables());
                    cancel();
                }
            }
        },0,1000); //Time is in milliseconds


    }

    public String changeInVariables() {

        if(currentSteps == steps) {
            double differenceS = ((int)((susceptible - S0)*100))/100;
            double differenceI = ((int)((infected - I0)*100))/100;
            double differenceR = ((int)((recovered - R0)*100))/100;
            return "\nChanges: " + differenceS + " susceptible, " + differenceI
                    + " infected, " + differenceR + " recovered.";
        }

        s_dot = -k * susceptible * infected + (mu * (1 - susceptible));
        i_dot = k * susceptible * infected - (gamma + mu) * infected;
        r_dot = gamma * infected - mu * recovered;

        susceptible += (s_dot) * stepSize;
        infected += (i_dot) * stepSize;
        recovered += (r_dot) * stepSize;

        susceptible = Math.max(susceptible, 0);
        infected = Math.max(infected, 0);
        recovered = Math.max(recovered, 0);

        return "There are " + susceptible + " people susceptible, " +
                infected + " infected people and " + recovered + " recovered people.";


    }

    public static void main(String[] args) {

    SIR_Model random = new SIR_Model(10,1,0,5);

    }

}
